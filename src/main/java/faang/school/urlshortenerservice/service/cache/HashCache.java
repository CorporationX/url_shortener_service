package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${hash-cache.capacity}")
    private int capacity;

    @Value("${hash-cache.fill-percent}")
    private double fillPercent;

    @Value("${hash-cache.cache-size}")
    private int cacheSize;

    private final HashGenerator hashGenerator;

    private Queue<Hash> cache;

    private final AtomicBoolean filling = new AtomicBoolean(false);

    @PostConstruct
    public void unit() {
        cache = new ArrayBlockingQueue<>(cacheSize);
        cache.addAll(hashGenerator.getHashes(capacity));
    }

    public Hash getHash() {
        double percent = (double) cache.size() / 100;
        if (percent < fillPercent) {
            fulfillCache();
        }
        return cache.poll();
    }

    @Async("getThreadPool")
    public void fulfillCache() {
        if (filling.compareAndSet(false, true)) {
            cache.addAll(hashGenerator.getHashes(capacity));
            filling.set(false);
        }
    }
}
