package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.CacheInitializationException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashService hashService;
    private final HashGenerator hashGenerator;
    private final ExecutorService executorService;

    private final AtomicBoolean isRefilledCache = new AtomicBoolean(false);

    private BlockingQueue<String> localCache;

    @Value("${hash.cache.capacity:1000}")
    private int cacheCapacity;

    @Value("${hash.cache.refill.threshold:0.2}")
    private double refillThreshold;

    @PostConstruct
    public void cacheInit() {
        localCache = new ArrayBlockingQueue<>(cacheCapacity);

        try {
            log.info("Starting initialization local cache...");
            if (hashService.isNeedGenerateHash()) {
                hashGenerator.generateBatch();
            }
            localCache.addAll(hashService.getHashBatch(cacheCapacity));
            log.info("Successfully initialization local cache");
        } catch (Exception exception) {
            log.error("Failed local cache initialization", exception);
            throw new CacheInitializationException("Cache init error: " + exception.getMessage());
        }
    }

    public Optional<String> getHash() {
        if (isNeedRefillCache() && isRefilledCache.compareAndSet(false, true)) {
            refillCache();
        }
        return Optional.ofNullable(localCache.poll());
    }

    private void refillCache() {
        log.info("Starting refill local cache...");
        int batchSize = cacheCapacity - localCache.size();
        CompletableFuture.supplyAsync(() -> hashService.getHashBatch(batchSize), executorService)
                .thenAccept(localCache::addAll)
                .thenRun(() -> {
                    log.info("Cache refilled with {} hashes", batchSize);
                    isRefilledCache.set(false);

                    if (hashService.isNeedGenerateHash()) {
                        hashGenerator.generateBatchAsync();
                    }
                })
                .exceptionally(exception -> {
                    isRefilledCache.set(false);
                    log.error("Cache refill failed", exception);
                    return null;
                });
    }

    private boolean isNeedRefillCache() {
        return localCache.size() < cacheCapacity * refillThreshold;
    }
}

