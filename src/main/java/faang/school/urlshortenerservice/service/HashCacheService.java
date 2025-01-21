package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCacheService {
    private final ConcurrentLinkedQueue<String> hashCache;
    private final ThreadPoolTaskExecutor shortenerTaskExecutor;
    private final HashService hashService;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);
    private final HashGenerator hashGenerator;

    @Value("${hash-properties.cache-capacity}")
    private Long cacheCapacity;

    @Value("${hash-properties.cache-threshold-rate}")
    private double lowThresholdRate;

    public String getHash() {
        if (isCacheLow()) {
            asyncCacheRefill();
        }

        return hashCache.poll();
    }

    public CompletableFuture<Void> asyncCacheRefill() {
        if (isRefilling.compareAndSet(false, true)) {
            return CompletableFuture.runAsync(() -> {
                try {
                    hashCache.addAll(hashService.getAndDeleteHashBatch(cacheCapacity));
                    hashGenerator.asyncHashRepositoryRefill();
                    log.info("Hash cache has been refilled by {} hashes", cacheCapacity);
                } finally {
                    isRefilling.set(false);
                }
            }, shortenerTaskExecutor);
        }

        return CompletableFuture.completedFuture(null);
    }

    private boolean isCacheLow() {
        return hashCache.size() < cacheCapacity * lowThresholdRate;
    }
}
