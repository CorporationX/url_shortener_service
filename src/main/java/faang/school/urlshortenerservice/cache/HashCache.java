package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Setter
public class HashCache {
    @Value("${cache.capacity}")
    private int capacity;
    @Value("${cache.low_fill_percentage}")
    private int lowFillPercentage;

    private final HashGenerator hashGenerator;
    private final Queue<String> hashesCache = new ArrayDeque<>(capacity);
    private final ExecutorService cachePool;
    private final AtomicBoolean running = new AtomicBoolean(false);


    @PostConstruct
    private void init() {
        hashesCache.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if ((hashesCache.size() / capacity) * 100 < lowFillPercentage) {
            if (running.compareAndSet(false, true)) {
                CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> hashGenerator.getHashes(capacity-hashesCache.size())
                        , cachePool);
                future.thenAccept(hashesCache::addAll).thenRun(() -> running.set(false)).join();
            }
        }
        return hashesCache.poll();
    }
}
