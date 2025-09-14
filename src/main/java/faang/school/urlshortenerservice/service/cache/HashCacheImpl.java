package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashCacheImpl implements HashCache {
    @Value("${hash.cache.capacity}")
    private int cacheCapacity;

    @Value("${hash.cache.min-limit-percent}")
    private int minLimitPercent;
    private final Executor executor;
    private final HashGenerator generator;
    private final HashRepository hashRepository;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private Queue<String> caches;

    @PostConstruct
    public void init() {
        try {
            log.info("Initializing cache with capacity: {}", cacheCapacity);
            caches = new ArrayBlockingQueue<>(cacheCapacity);
            List<String> hashes = generator.generateBatch();
            caches.addAll(hashes);
            log.info("Initialized cache with {} hashes", hashes.size());
        } catch (Exception e) {
            log.error("Failed to initialize cache", e);
            throw new RuntimeException("Error initializing cache", e);
        }
    }

    @Override
    public String getHash() {
        try {
            if (isBelowLimit()) {
                log.debug("Cache size is below limit. Current size: {}, capacity: {}", caches.size(), cacheCapacity);
                if (!isFilling.compareAndSet(false, true)) {
                    log.debug("Another refill operation is in progress");
                    return caches.poll();
                }
                CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                    try {
                        int fetchSize = cacheCapacity - caches.size();
                        log.debug("Fetching {} hashes from repository", fetchSize);
                        List<String> hashBatch = hashRepository.getHashBatch(fetchSize);
                        caches.addAll(hashBatch);
                        if (hashBatch.size() < fetchSize) {
                            log.debug("Generating additional hashes. Fetched: {}, required: {}", hashBatch.size(), fetchSize);
                            generator.generateBatch();
                        }
                        log.debug("Refill operation completed. New cache size: {}", caches.size());
                    } catch (Exception e) {
                        log.error("Error during refill operation", e);
                        throw e;
                    } finally {
                        isFilling.set(false);
                    }
                }, executor);
            }
            String hash = caches.poll();
            if (hash == null) {
                log.warn("Cache is empty, unable to provide hash");
            }
            return hash;
        } catch (Exception e) {
            log.error("Error getting hash from cache", e);
            throw new RuntimeException("Error getting hash", e);
        }
    }

    private boolean isBelowLimit() {
        return (double) caches.size() / cacheCapacity * 100 <= minLimitPercent;
    }
}