package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
@Repository
public class HashCache {
    private final HashService hashService;
    private final ExecutorService hashGenerateExecutorService;
    @Value("${hash-generator.local-cache-size:1000}")
    private int cacheSize;
    private Queue<String> cache;
    @Value("${hash-generator.percent-free-hashes:20}")
    private int percentFreeHashes = 20;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(cacheSize);
        List<String> hashes = hashService.getHashes(cacheSize);
        cache.addAll(hashes);
        log.info("Hash cache initialized");
    }

    public String getHash() {

        if ((100.0 * cache.size() / cacheSize) < percentFreeHashes) {
            int count = cacheSize - cache.size();
            CompletableFuture.supplyAsync(() -> hashService.getHashes(count), hashGenerateExecutorService)
                    .thenAccept(cache::addAll);
        }
        return cache.poll();
    }
}
