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

    @Value("${services.hash.cache-size}")
    private int cacheSize;

    @Value("${services.hash.fill_percent}")
    private int minFreeHashesPercent;

    private final AtomicBoolean filling = new AtomicBoolean(false);
    private Queue<String> hashes;
    private final HashService hashService;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(cacheSize);
        fillCacheSynchronously();
    }

    public String getHash() {
        checkAndRefillCacheIfNeeded();
        return hashes.poll();
    }

    private void checkAndRefillCacheIfNeeded() {
        if (isCacheBelowMinFreeHashesPercent() && filling.compareAndSet(false, true)) {
            hashService.getHashesAsync((long) cacheSize)
                    .thenAccept(this::addHashesToCache)
                    .thenRun(() -> filling.set(false));
        }
    }

    private boolean isCacheBelowMinFreeHashesPercent() {
        return hashes.size() * 100.0 / cacheSize < minFreeHashesPercent;
    }

    private void fillCacheSynchronously() {
        hashes.addAll(hashService.getHashes((long) cacheSize));
    }

    private void addHashesToCache(List<String> newHashes) {
        hashes.addAll(newHashes);
    }
}