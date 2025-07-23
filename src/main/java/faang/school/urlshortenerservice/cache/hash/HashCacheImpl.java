package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.cache.HashCache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCacheImpl implements HashCache {
    private final ExecutorService hashCachePool;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash.cache.min_percent}")
    private int minPercent;
    @Value("${hash.cache.max_cache_size}")
    private int maxCacheSize;
    private final ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public String getHash() {
        String hash = cache.poll();
        if (hash != null) {
            if (lessThenMin()) {
                log.debug("Cache is less Then min. Will refill");
                refillCache();
            }
            return hash;
        }
        log.warn("Cache is Empty, generate hashes");
        return getHashNow();
    }

    private boolean lessThenMin() {
        return cache.size() < (maxCacheSize / 100 * minPercent);
    }
    private String getHashNow() {
        List<String> hashes = hashRepository.getHashBatch();
        cache.addAll(hashes);
        return cache.poll();
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