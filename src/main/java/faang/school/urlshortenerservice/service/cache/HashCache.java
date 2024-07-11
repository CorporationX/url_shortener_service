package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashCache {

    @Value("${services.hash.cache.size}")
    private int cacheSize;

    @Value("${services.hash.fillingPercent}")
    private int fillingPercent;

    private Queue<String> hashes;
    private final HashService hashService;
    private final AtomicBoolean isCacheFilled = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(cacheSize);
        try {
            fillCacheSynchronously();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize HashCache", e);
        }
    }

    public String getHash() {
        checkAndRefillCacheIfNeeded();
        return hashes.poll();
    }

    private void checkAndRefillCacheIfNeeded() {
        if (isCacheBelowMinFreeHashesPercent() && isCacheFilled.compareAndSet(false, true)) {
            hashService.getHashesAsync(cacheSize)
                    .thenAccept(this::addHashesToCache)
                    .thenRun(() -> isCacheFilled.set(false));
            log.info("Filled cache");
        }
    }

    private boolean isCacheBelowMinFreeHashesPercent() {
        return hashes.size() * 100.0 / cacheSize < fillingPercent;
    }

    private void fillCacheSynchronously() {
        try {
            hashService.generateHashes().join();
            hashes.addAll(hashService.getHashes(cacheSize));
        } catch (Exception e) {
            throw new RuntimeException("Failed to fill cache synchronously", e);
        }
    }

    private void addHashesToCache(List<String> newHashes) {
        hashes.addAll(newHashes);
    }
}