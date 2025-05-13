package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.component.HashGenerator;
import faang.school.urlshortenerservice.config.app.HashCacheConfig;
import faang.school.urlshortenerservice.config.app.HashGeneratorConfig;
import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class HashCache {

    private final HashCacheConfig config;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService hashCacheExecutor;
    private final HashGeneratorConfig hashGeneratorConfig;

    private final Queue<String> cache = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        log.info("Starting initial database and cache population on application startup");
        try {
            int maxSize = config.getMaxSize();
            log.info("Initial setup: aiming to fill cache to maxSize = {}", maxSize);

            CompletableFuture<Void> populateDbFuture = populateDatabaseAsync();

            populateDbFuture.thenRunAsync(() -> {
                log.info("Database population completed, starting cache fill");
                fillCacheAsync();
            }, hashCacheExecutor);
        } catch (Exception e) {
            log.error("Failed to populate database and cache on startup", e);
        }
    }

    private CompletableFuture<Void> populateDatabaseAsync() {
        return CompletableFuture.runAsync(() -> {
            int initialDbBatchSize = config.getInitialDbSize();
            int batchSize = hashGeneratorConfig.getBatchSize();
            int batches = (int) Math.ceil((double) initialDbBatchSize / batchSize);
            log.info("Populating database with {} hashes in {} batches", initialDbBatchSize, batches);

            CountDownLatch latch = new CountDownLatch(batches);
            for (int i = 0; i < batches; i++) {
                final int batchNumber = i + 1;
                hashCacheExecutor.submit(() -> {
                    try {
                        log.info("Starting batch {} of {}", batchNumber, batches);
                        hashGenerator.generateBatch();
                        log.info("Completed batch {} of {}", batchNumber, batches);
                    } catch (Exception e) {
                        log.error("Error generating batch {} of {}", batchNumber, batches, e);
                    } finally {
                        latch.countDown();
                        log.debug("Latch count after batch {}: {}", batchNumber, latch.getCount());
                    }
                });
            }

            try {
                if (!latch.await(10, TimeUnit.SECONDS)) {
                    log.warn("Database population timed out after 10 seconds");
                } else {
                    log.info("Database population completed");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while populating database", e);
            }
        }, hashCacheExecutor);
    }

    private CompletableFuture<Void> fillCacheAsync() {
        return CompletableFuture.runAsync(() -> {
            int maxSize = config.getMaxSize();
            log.info("Filling cache to maxSize: {}", maxSize);

            int maxAttempts = 50;
            int attempts = 0;

            while (cache.size() < maxSize && attempts < maxAttempts) {
                log.debug("Attempt {} to fill cache, current size: {}", attempts + 1, cache.size());
                List<String> newHashes = hashRepository.getHashBatch();
                if (newHashes.isEmpty()) {
                    log.debug("No hashes available yet, waiting...");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("Interrupted during cache fill", e);
                    }
                    attempts++;
                    continue;
                }
                cache.addAll(newHashes);
                log.info("Added {} hashes to cache, current size: {}", newHashes.size(), cache.size());
                attempts = 0;
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
        if (hash == null) {
            log.warn("Cache is empty, returning null");
            return null;
        }
        log.debug("Returning hash: {}", hash);

        int currentSize = cache.size();
        int maxSize = config.getMaxSize();
        int threshold = (int) (maxSize * (config.getRefillThreshold() / 100.0));
        if (currentSize < threshold && isRefilling.compareAndSet(false, true)) {
            log.info("Cache size {}/{} below threshold ({}), starting async refill",
                    currentSize, maxSize, threshold);
            hashCacheExecutor.submit(this::refillCache);
        }

        return hash;
    }

    private void refillCache() {
        try {
            int toFetch = config.getMaxSize() - cache.size();
            if (toFetch <= 0) {
                log.info("Cache already full, skipping fetch");
                return;
            }

            log.info("Fetching up to {} hashes from repository", toFetch);
            while (toFetch > 0) {
                List<String> newHashes = hashRepository.getHashBatch();
                if (newHashes.isEmpty()) {
                    log.info("No more hashes available from repository, stopping fetch");
                    break;
                }
                cache.addAll(newHashes);
                toFetch = config.getMaxSize() - cache.size();
                log.info("Added {} hashes to cache, current size: {}, remaining to fetch: {}",
                        newHashes.size(), cache.size(), toFetch);
            }

            int toGenerate = config.getMaxSize() - cache.size();
            if (toGenerate > 0) {
                int batchSize = hashGeneratorConfig.getBatchSize();
                int batches = (int) Math.ceil((double) toGenerate / batchSize);
                log.info("Triggering {} batches to generate ~{} hashes in DB", batches, toGenerate);
                for (int i = 0; i < batches; i++) {
                    hashCacheExecutor.submit(() -> hashGenerator.generateBatch());
                }
            }
        } catch (Exception e) {
            log.error("Error refilling cache", e);
        } finally {
            isRefilling.set(false);
            log.info("Refill completed, isRefilling reset to false");
        }
    }
}
