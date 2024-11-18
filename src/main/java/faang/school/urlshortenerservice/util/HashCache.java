package faang.school.urlshortenerservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {
    private final int cacheCapacity;
    private final int threshold;
    private final int batchSize;

    private final HashGenerator hashGenerator;
    private final ExecutorService fillUpCacheExecutorService;
    private final AtomicBoolean isFillingUp = new AtomicBoolean(false);
    private final Queue<String> cache;

    public HashCache(
            @Value("${app.hash-cache.size:1000}") int cacheCapacity,
            @Value("${app.hash-cache.threshold:200}") int threshold,
            @Value("${app.hashes-generation.batch-size:800}") int batchSize,
            HashGenerator hashGenerator,
            ExecutorService fillUpCacheExecutorService) {
        this.cacheCapacity = cacheCapacity;
        this.threshold = threshold;
        this.batchSize = batchSize;
        this.hashGenerator = hashGenerator;
        this.fillUpCacheExecutorService = fillUpCacheExecutorService;
        this.cache = new ConcurrentLinkedDeque<>();

        hashGenerator.generateBatchOfHashes(cacheCapacity + batchSize);
        List<String> hashesFromDb = hashGenerator.getHashes(cacheCapacity);
        cache.addAll(hashesFromDb);
    }

    public String getHash() {
        fillUpCacheIfNeeded();
        return cache.poll();
    }

    private void fillUpCacheIfNeeded() {
        if (cache.size() < threshold && isFillingUp.compareAndSet(false, true)) {
            fillUpCacheExecutorService.execute(() -> {
                try {
                    fillUpCache(batchSize);
                } finally {
                    isFillingUp.set(false);
                }
            });
        }
    }

    private void fillUpCache(int batchSize) {
        List<String> hashesFromDb = hashGenerator.getHashes(batchSize);
        cache.addAll(hashesFromDb);
        hashGenerator.generateBatchOfHashesAsync(cacheCapacity);
    }
}
