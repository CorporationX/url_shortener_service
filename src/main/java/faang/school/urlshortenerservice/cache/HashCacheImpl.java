package faang.school.urlshortenerservice.cache;


import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class HashCacheImpl implements HashCache {

    private final Executor executorForHashCache;
    private final HashRepository hashRepository;
    private final HashCacheProperty cacheProperty;
    private final HashGenerator generator;

    private final Queue<String> hashQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    private int threshold;

    @PostConstruct
    void initHashCache() {
        generator.generateBatch();
        int currentSize = hashQueue.size();
        threshold = (int) (cacheProperty.getMaxQueueSize() * (cacheProperty.getRefillThresholdPercent() / 100.0));
        refillCacheAsync(currentSize, threshold);
    }

    public String getHash() {
        int currentSize = hashQueue.size();

        if (currentSize < threshold && isRefilling.compareAndSet(false, true)) {
            refillCacheAsync(currentSize, threshold);
        }
        return hashQueue.poll();
    }

    private void refillCacheAsync(int currentSize, int threshold) {
        executorForHashCache.execute(() -> {
            log.debug("Current queue size: {}. Refill threshold: {}", currentSize, threshold);
            refillingCacheWithHashes(cacheProperty.getMaxQueueSize() - hashQueue.size());
            isRefilling.set(false);
            log.info("Cache successfully refilled.");
        });
    }

    private void refillingCacheWithHashes(int limit) {
        hashQueue.addAll(hashRepository.findTopNHashes(limit));
        generator.generateBatch();
    }
}
