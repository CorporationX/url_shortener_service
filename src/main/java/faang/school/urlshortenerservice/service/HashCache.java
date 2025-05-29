package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashService hashService;
    private final ExecutorService executorService;

    @Value("${hash-generator.cache-size}")
    private int cacheSize;

    @Value("${hash-generator.min-free-ratio-hashes}")
    private double minFreeRatio;

    private final ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();

    @PostConstruct
    public void init() {
        log.info("Initializing HashCache with size: {}", cacheSize);
        refillCache();
    }

    public String getHash() {
        checkAndRefillCacheAsync();
        String hash = cache.poll();
        if (hash == null) {
            log.warn("Hash cache is empty, attempting synchronous refill");
            refillCache();
            hash = cache.poll();
            if (hash == null) {
                throw new IllegalStateException("Failed to obtain hash from cache");
            }
        }
        log.debug("Retrieved hash: {}", hash);
        return hash;
    }

    private void checkAndRefillCacheAsync() {
        if (shouldRefillCache()) {
            synchronized (this) {
                if (shouldRefillCache()) {
                    executorService.submit(this::refillCache);
                    log.info("Submitted async cache refill task");
                }
            }
        }
    }

    private void refillCache() {
        try {
            int targetSize = cacheSize - cache.size();
            if (targetSize <= 0) {
                log.debug("Cache is sufficiently filled, size: {}", cache.size());
                return;
            }
            log.info("Refilling cache, target size: {}", targetSize);
            List<String> hashes = hashService.getHashes(targetSize);
            cache.addAll(hashes);
            log.info("Cache refilled, current size: {}", cache.size());
        } catch (Exception e) {
            log.error("Error refilling cache", e);
            throw new IllegalStateException("Failed to refill hash cache", e);
        }
    }

    private boolean shouldRefillCache() {
        double currentRatio = (double) cache.size() / cacheSize;
        return currentRatio < minFreeRatio;
    }
}
