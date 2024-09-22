package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    @Value("${cache.capacity}")
    private int capacity;
    @Value("${cache.low_fill_percentage}")
    private int lowFillPercentage;

    private final HashGenerator hashGenerator;
    private Queue<String> hashesCache;
    private final ExecutorService cachePool;
    private final AtomicBoolean running = new AtomicBoolean(false);


    @PostConstruct
    public void init() {
        this.hashesCache = new LinkedBlockingQueue<>(capacity);
        hashesCache.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (hashesCache.size() * 100 / capacity < lowFillPercentage) {
            log.info("Low fill percentage: {}", lowFillPercentage);
            if (running.compareAndSet(false, true)) {
                CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> hashGenerator.getHashes(capacity - hashesCache.size())
                        , cachePool);
                future.thenAccept(hashesCache::addAll).thenRun(() -> running.set(false)).join();
                log.info("hashes cache was additionally filled");
            }
        }
        return hashesCache.poll();
    }
}
