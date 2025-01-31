package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.CacheUpdateException;
import faang.school.urlshortenerservice.exception.HashRetrievalException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    @Value("${hash-cache.queue-capacity}")
    private int queueCapacity;
    @Value("${hash-cache.percent}")
    private double percent;
    @Value("${hash-cache.redis-batch-size}")
    private int redisBatchSize;
    @Value("${hash-cache.cache-fill-timeout}")
    private int cacheFillTimeout;

    private final AtomicBoolean isCacheFilling = new AtomicBoolean(false);
    private final ThreadPoolTaskExecutor taskExecutor;
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private BlockingQueue<Hash> caches;

    @PostConstruct
    public void init() {
        caches = new ArrayBlockingQueue<>(queueCapacity);
        try {
            hashGenerator.generateBatch();
            List<Hash> hashes = hashRepository.getHashBatch(redisBatchSize);
            caches.addAll(hashes);
            log.info("Cache successfully initialized during startup");
        } catch (Exception e) {
            log.error("Unexpected error during cache initialization", e);
            throw new IllegalStateException("Unexpected error during cache initialization", e);
        }
    }

    public Hash getHash() {
        if (caches.size() <= redisBatchSize * percent) {
            if (isCacheFilling.compareAndSet(false, true)) {
                CompletableFuture.runAsync(() -> {
                    try {
                        hashGenerator.generateBatch();
                        caches.addAll(hashRepository.getHashBatch(redisBatchSize));
                    } catch (Exception e) {
                        throw new CacheUpdateException("Failed to update cache with new hashes: " + e.getMessage(), e);
                    } finally {
                        isCacheFilling.set(false);
                    }
                }, taskExecutor);
            }
        }
        try {
            Hash hash = caches.poll(cacheFillTimeout, TimeUnit.MILLISECONDS);
            if (hash == null) {
                log.warn("Cache retrieval timeout reached, generating hashes synchronously.");
                generateHashesSynchronously();
                return caches.take();
            }
            return hash;

        } catch (InterruptedException e) {
            throw new HashRetrievalException("Failed to retrieve hash from the queue: " + e.getMessage(), e);
        }

    }

    private void generateHashesSynchronously() {
        try {
            hashGenerator.generateBatch();
            caches.addAll(hashRepository.getHashBatch(redisBatchSize));
        } catch (Exception e) {
            throw new CacheUpdateException("Failed to update cache with new hashes: " + e.getMessage(), e);
        }
    }
}
