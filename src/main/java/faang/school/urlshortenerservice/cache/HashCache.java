package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.hash.HashGenerator;
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
    @Value("${cache.capacity}")
    private int sizeQueue;
    @Value("${cache.fullness}")
    private double fullness;
    private  Queue<String> cache;

    private final HashGenerator hashGenerator;
    private final AtomicBoolean generateCacheHashes = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(sizeQueue);
        cache.addAll(hashGenerator.getHashes(sizeQueue));
    }

    public String getHash() {
        updateCache();
        return cache.poll();
    }
    @Async("executorService")
    public void updateCache() {
        double percent = (double) cache.size() / 100;
        if (percent < fullness) {
            if (generateCacheHashes.compareAndSet(false, true)) {
                hashGenerator.generatedBatchAsync(sizeQueue)
                        .thenAccept(hashes -> cache.addAll(hashes))
                        .thenRun(() -> generateCacheHashes.set(false));
            }
        }
    }
}
