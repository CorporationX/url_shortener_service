package faang.school.urlshortenerservice.component;

import faang.school.urlshortenerservice.config.app.HashCacheProperties;
import faang.school.urlshortenerservice.exception.NoHashAvailableException;
import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCache {

    private final HashCacheProperties properties;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService hashCacheExecutor;

    private final Queue<String> cache = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @PostConstruct
    private void init() {
        log.debug("Starting initial database and cache population on application startup");
        int maxSize = properties.getMaxSize();
        log.debug("Initial setup: aiming to fill cache to maxSize = {}", maxSize);

        CompletableFuture<Void> populateDbFuture = populateDatabaseAsync();
        populateDbFuture.thenRunAsync(() -> {
            log.debug("Database population completed, starting cache fill");
            fillCacheAsync();
        }, hashCacheExecutor);
    }

    private CompletableFuture<Void> populateDatabaseAsync() {
        int initialDbSize = properties.getInitialDbSize();
        log.debug("Populating database with {} hashes", initialDbSize);
        return hashGenerator.generateHashes(initialDbSize);
    }

    private CompletableFuture<Void> fillCacheAsync() {
        return CompletableFuture.runAsync(() -> {
            int maxSize = properties.getMaxSize();
            log.debug("Filling cache to maxSize: {}", maxSize);

            int toFetch = maxSize - cache.size();
            while (cache.size() < maxSize && toFetch > 0) {
                List<String> newHashes = hashRepository.getHashBatch();
                if (newHashes.isEmpty()) {
                    log.info("No more hashes available from repository, stopping fetch");
                    break;
                }
                cache.addAll(newHashes);
                toFetch = maxSize - cache.size();
                log.debug("Added {} hashes to cache, current size: {}, remaining to fetch: {}",
                        newHashes.size(), cache.size(), toFetch);
            }

            if (cache.size() < maxSize) {
                log.warn("Failed to fill cache to maxSize, current size: {}", cache.size());
            } else {
                log.info("Cache filled to maxSize: {}", cache.size());
            }
        }, hashCacheExecutor);
    }

    public String getHash() {
        String hash = cache.poll();
        if (hash != null) {
            log.debug("Returning hash from cache: {}", hash);
            checkAndRefillIfNeeded();
            return hash;
        }

        log.info("Cache is empty, attempting to fetch from database");
        List<String> newHashes = hashRepository.getHashBatch();
        if (!newHashes.isEmpty()) {
            log.info("Fetched {} hashes from database, adding to cache", newHashes.size());
            cache.addAll(newHashes);
            hash = cache.poll();
            if (hash != null) {
                log.debug("Returning hash after fetching from database: {}", hash);
                checkAndRefillIfNeeded();
                return hash;
            }
        }

        log.warn("No hashes available in database, triggering async refill");
        if (isRefilling.compareAndSet(false, true)) {
            hashCacheExecutor.submit(this::refillCache);
        }

        throw new NoHashAvailableException("No hashes available, refill started asynchronously");
    }

    private void checkAndRefillIfNeeded() {
        int currentSize = cache.size();
        int maxSize = properties.getMaxSize();
        int threshold = (int) (maxSize * (properties.getRefillThreshold() / 100.0));
        if (currentSize < threshold && isRefilling.compareAndSet(false, true)) {
            log.info("Cache size {}/{} below threshold ({}), starting async refill",
                    currentSize, maxSize, threshold);
            hashCacheExecutor.submit(this::refillCache);
        }
    }

    private void refillCache() {
        int toFetch = properties.getMaxSize() - cache.size();
        if (toFetch <= 0) {
            log.info("Cache already full, skipping fetch");
            return;
        }

        log.debug("Refilling cache, need to fetch up to {} hashes", toFetch);

        try {
            List<String> newHashes = hashRepository.getHashBatch();
            if (!newHashes.isEmpty()) {
                cache.addAll(newHashes);
                log.info("Added {} hashes to cache from database, current size: {}", newHashes.size(), cache.size());
                toFetch = properties.getMaxSize() - cache.size();
            }

            if (toFetch > 0) {
                log.info("Not enough hashes in database, generating {} new hashes", toFetch);
                hashGenerator.generateHashes(toFetch).join();
                log.debug("Generation complete, fetching new hashes");

                newHashes = hashRepository.getHashBatch();
                if (!newHashes.isEmpty()) {
                    cache.addAll(newHashes);
                    log.info("Added {} generated hashes to cache, current size: {}", newHashes.size(), cache.size());
                } else {
                    log.warn("No hashes retrieved after generation");
                }
            }

            if (cache.size() < properties.getMaxSize()) {
                throw new NoHashAvailableException("Failed to fill cache to maxSize, current size: " + cache.size());
            } else {
                log.debug("Cache successfully refilled to maxSize: {}", cache.size());
            }
        } finally {
            isRefilling.set(false);
            log.debug("Refill completed, isRefilling reset to false");
        }
    }
}