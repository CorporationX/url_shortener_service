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
    @Value("${app.hash-cache.size:1000}")
    private int cacheCapacity;

    @Value("${app.hash-cache.threshold:200}")
    private int threshold;

    @Value("${app.hashes-generation.batch-size:800}")
    private int batchSize;

    private final HashGenerator hashGenerator;
    private final AtomicBoolean isFillingUp = new AtomicBoolean(false);
    private final ExecutorService fillUpCacheExecutorService;

    private Queue<String> cache;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(cacheCapacity);
        hashGenerator.generateBatchOfHashes(cacheCapacity + batchSize);
        List<String> hashesFromDb = hashGenerator.getHashes(cacheCapacity);
        cache.addAll(hashesFromDb);
    }

    public String getHash() {
        fillUpCacheIfNeeded();
        return cache.poll();
    }

    private void fillUpCacheIfNeeded() {
        if (isFillingUp.compareAndSet(false, true) && cache.size() < threshold) {
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
