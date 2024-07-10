package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.encoder.Base62Encoder;
import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashCache {

    private static final Logger log = LoggerFactory.getLogger(HashCache.class);
    @Value("${services.hash.cache.size}")
    private int cacheSize;

    @Value("${services.hash.min_free_hashes_percent}")
    private int minFreeHashesPercent;

    private final AtomicBoolean isCacheFilled = new AtomicBoolean(false);
    private Queue<String> hashes;
    private final HashService hashService;
    private final Base62Encoder encoder;
    private final HashRepository hashRepository;

    @PostConstruct
    public void init() throws IllegalStateException {
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
        return hashes.size() * 100.0 / cacheSize < minFreeHashesPercent;
    }

    private void fillCacheSynchronously() throws RuntimeException {
        try {
            hashService.generateHashes();
            hashes.addAll(hashService.getHashes(cacheSize));
        } catch (Exception e) {
            throw new RuntimeException("Failed to fill cache synchronously", e);
        }
    }

    private void addHashesToCache(List<String> newHashes) {
        hashes.addAll(newHashes);
    }
}