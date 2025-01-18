package faang.school.urlshortenerservice.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${app.hash.cache.capacity:1000}")
    private int cacheCapacity;

    @Value("${app.hash.cache.min-fill-percent:20}")
    private int fillPercent;
    private Queue<String> cache;

    private final HashGenerator hashGenerator;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @PostConstruct
    private void init() {
        cache = new ArrayBlockingQueue<>(cacheCapacity);
        cache.addAll(hashGenerator.getHashes(cacheCapacity));
    }

    public String getHash() {
        if (cache.size() / (cacheCapacity / 100.0) < fillPercent) {
            if (isFilling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(cacheCapacity)
                        .thenAccept(cache::addAll)
                        .thenRun(() -> isFilling.set(false));
            }
        }

        return cache.poll();
    }
}
