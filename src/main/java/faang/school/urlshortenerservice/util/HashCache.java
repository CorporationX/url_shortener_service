package faang.school.urlshortenerservice.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Component
public class HashCache {
    @Value("${app.hash-cache.size:100}")
    private int cacheCapacity;

    @Value("${app.hash-cache.threshold:20}")
    private int threshold;

    @Value("${app.hashes-generation.batch-size:80}")
    private int batchSize;

    private final HashGenerator hashGenerator;
    private final AtomicBoolean isGenerating = new AtomicBoolean(false);
    private final ExecutorService fillUpCacheExecutorService;

    private Queue<String> cache;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(cacheCapacity);
        fillUpCache(cacheCapacity);
    }

    public String getHash() {
        fillUpCacheIfNeeded();
        return cache.poll();
    }

    private void fillUpCacheIfNeeded() {
        if (cacheSizeLessThanThreshold() && isGenerating.compareAndSet(false, true)) {
            fillUpCacheExecutorService.execute(() -> {
                try {
                    fillUpCache(batchSize);
                } finally {
                    isGenerating.set(false);
                }
            });
        }
    }

    private boolean cacheSizeLessThanThreshold() {
        return (100 / cacheCapacity) * cache.size() < threshold;
    }

    private void fillUpCache(int batchSize) {
        hashGenerator.generateBatchOfHashes(batchSize);
        List<String> hashesFromDb = hashGenerator.getHashes(batchSize);
        cache.addAll(hashesFromDb);
    }
}
