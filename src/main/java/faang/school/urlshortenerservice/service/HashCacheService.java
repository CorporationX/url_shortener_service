package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCacheService {
    private final Deque<String> hashCache = new ConcurrentLinkedDeque<>();
    private final ExecutorService executorService;
    private final HashService hashService;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @Value("${hash-properties.cache-capacity}")
    private Long cacheCapacity;

    @Value("${hash-properties.threshold-percent}")
    private int lowThresholdPercent;

    public String getHash() {
        if (isCacheLow()) {
            asyncCacheRefill();
        }

        return hashCache.pop();
    }

    public void asyncCacheRefill() {
        CompletableFuture.runAsync(() -> {
            if (isRefilling.compareAndExchange(false, true)) {
                try {
                    hashCache.addAll(hashService.getAndDeleteHashBatch(cacheCapacity));
                    log.info("Hash cache has been refilled by {} hashes", cacheCapacity);
                } finally {
                    isRefilling.set(false);
                }
            }
        }, executorService);
    }

    private boolean isCacheLow() {
        return hashCache.size() < cacheCapacity * 100 / lowThresholdPercent;
    }
}
