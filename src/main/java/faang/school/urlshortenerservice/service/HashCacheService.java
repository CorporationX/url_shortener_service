package faang.school.urlshortenerservice.service;

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
        return CompletableFuture.runAsync(() -> {
            if (isRefilling.compareAndSet(false, true)) {
                try {
                    hashCache.addAll(hashService.getAndDeleteHashBatch(cacheCapacity));
                    log.info("Hash cache has been refilled by {} hashes", cacheCapacity);
                } finally {
                    isRefilling.set(false);
                }
            }
        }, shortenerTaskExecutor);
    }

    private boolean isCacheLow() {
        return hashCache.size() < cacheCapacity * lowThresholdRate;
    }
}
