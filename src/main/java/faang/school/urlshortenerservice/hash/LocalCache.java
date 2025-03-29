package faang.school.urlshortenerservice.hash;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class LocalCache {
    private final HashGenerator hashGenerator;
    private final AsyncHashGenerator asyncHashGenerator;

    private final AtomicBoolean isCacheFillingUp = new AtomicBoolean(false);

    @Value("${cache.capacity:10000}")
    private int cacheCapacity;

    @Value("${cache.min-percentage-filling:20}")
    private int cacheMinPercentageFilling;

    private Queue<String> cache;

    @PostConstruct
    public void init() {
        if (cacheCapacity < 1) {
            throw new IllegalArgumentException("Cache capacity must be positive");
        }
        cache = new ArrayBlockingQueue<>(cacheCapacity);
        cache.addAll(hashGenerator.getBatch(cacheCapacity));
    }

    public String getHash() {
        addHashesIfNecessary();
        return cache.peek();
    }

    private void addHashesIfNecessary() {
        if (cache.size() < calculateCachePercentageFilling()
                && isCacheFillingUp.compareAndSet(false, true)) {
            asyncHashGenerator.getBatchAsync(cacheCapacity - cache.size())
                    .thenAccept(hashes -> hashes.forEach(cache::offer))
                    .thenRun(() -> isCacheFillingUp.set(false));
        }
    }

    private double calculateCachePercentageFilling() {
        return cacheCapacity / 100.0 * cacheMinPercentageFilling;
    }
}
