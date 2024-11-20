package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash.cache.size}")
    private int cacheSize;

    @Value("${hash.fillingPercent}")
    private int fillingPercent;

    private Queue<String> hashes;
    private final HashService hashService;
    private final AtomicBoolean isCacheFilled = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(cacheSize);
        try {
            synchronouslyFillCache();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize HashCache", e);
        }
    }

    public String retrieveNextHash() {
        refillCacheIfNecessary();
        return hashes.poll();
    }

    private void refillCacheIfNecessary() {
        if (isCacheBelowThreshold() && isCacheFilled.compareAndSet(false, true)) {
            hashService.retrieveHashBatchAsync(cacheSize)
                    .thenAccept(this::addHashes)
                    .thenRun(() -> isCacheFilled.set(false));
        }
    }

    private boolean isCacheBelowThreshold() {
        return hashes.size() * 100.0 / cacheSize < fillingPercent;
    }

    private void synchronouslyFillCache() {
        try {
            hashService.generateAndStoreHashBatch().join();
            hashes.addAll(hashService.retrieveHashBatch(cacheSize));
        } catch (Exception e) {
            throw new RuntimeException("Failed to fill cache synchronously", e);
        }
    }

    private void addHashes(List<String> newHashes) {
        hashes.addAll(newHashes);
    }
}