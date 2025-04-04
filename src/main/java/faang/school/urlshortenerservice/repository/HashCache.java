package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
@Repository
public class HashCache {
    private final HashService hashService;
    @Resource(name = "getHashExecutorService")
    private final ExecutorService getHashExecutorService;
    @Value("${hash-generator.local-cache-size:1000}")
    private int cacheSize;
    private Queue<String> cache;
    @Value("${hash-generator.ratio-free-hashes:0.2}")
    private double ratioFreeHashes;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(cacheSize);
        List<String> hashes = hashService.getHashes(cacheSize);
        cache.addAll(hashes);
        log.info("Hash cache initialized");
    }

    public String getHash() {
        if (isCacheUnderLimit()) {
            getHashExecutorService.execute(fillCache());
        }
        return cache.poll();
    }

    private Runnable fillCache() {
        return () -> {
            int count = cacheSize - cache.size();
            List<String> hashes = hashService.getHashes(count);
            cache.addAll(hashes);
        };
    }

    private boolean isCacheUnderLimit() {
        return ((double) cache.size() / cacheSize) < ratioFreeHashes;
    }
}
