package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.cache.HashCache;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@AllArgsConstructor
public class HashCacheImpl implements HashCache {
    private final ExecutorService hashCachePool;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash.cache.min_percent}")
    private final int minPercent;
    @Value("${hash.cache.max_cache_size}")
    private final int maxCacheSize;
    private final ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public String getHash() {
        String hash = cache.poll();
        if (hash == null) {
            log.warn("Cache is empty");
            refillCache();
            return cache.poll();
        }

        if (lessThenMin()) {
            refillCache();
        }
        return hash;
    }

    private boolean lessThenMin() {
        return cache.size() < (maxCacheSize / 100 * minPercent);
    }

    private void refillCache() {
        if (!lock.tryLock()) {
            log.info("Lock detected/ Refill is already in process");
            return;
        }

        try {
            log.info("Async refill process started");
            CompletableFuture.supplyAsync(hashRepository::getHashBatch, hashCachePool)
                    .thenAccept(cache::addAll)
                    .thenRunAsync(hashGenerator::generateBatch, hashCachePool)
                    .thenRun(lock::unlock)
                    .exceptionally(throwable -> {
                        log.error("Cache refill failed", throwable);
                        lock.unlock();
                        return null;
                    });
        } catch (Exception e) {
            lock.unlock();
            log.error("Cache refill initialization failed", e);
        }
    }
}