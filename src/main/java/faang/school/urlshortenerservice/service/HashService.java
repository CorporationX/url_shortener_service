package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashService {
    private static final Queue<FreeHash> FREE_HASHES_CACHE = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean lock = new AtomicBoolean(false);

    private final FreeHashRepository freeHashRepository;
    private final FreeHashGenerator freeHashGenerator;
    private final ApplicationContext applicationContext;

    @Value("${shortener.hash-pool.max-postgres-capacity}")
    private long maxDbCapacity;

    @Value("${shortener.hash-pool.max-cache-capacity}")
    private long maxCacheCapacity;

    @Value("${shortener.hash-pool.refill-threshold-percent}")
    private int refillThresholdPercent;

    public FreeHash getAvailableHash() {
        triggerAsyncCacheRefillIfNeeded();
        return FREE_HASHES_CACHE.poll();
    }

    private void triggerAsyncCacheRefillIfNeeded() {
        long threshold = maxCacheCapacity * refillThresholdPercent / 100;
        if (FREE_HASHES_CACHE.size() < threshold) {
            applicationContext.getBean(HashService.class).asyncRefill();
        }
    }

    @Async("hashServiceExecutor")
    public void asyncRefill() {
        if (!lock.compareAndSet(false, true)) {
            return;
        }

        try {
            long availableHashes = FREE_HASHES_CACHE.size();
            long toRefill = maxCacheCapacity - availableHashes;
            applicationContext.getBean(HashService.class).refill(toRefill);
        } finally {
            lock.set(false);
        }
    }

    @Transactional
    public void refill(long toRefill) {
        long freeHashesInDb = freeHashRepository.count();
        if (freeHashesInDb < maxDbCapacity) {
            long refillDatabaseCount = maxDbCapacity + toRefill - freeHashesInDb;
            applicationContext.getBean(HashService.class).refillDbIfNeeded(refillDatabaseCount);
        }
        List<FreeHash> dbHashes = freeHashRepository.deleteAndReturnFreeHashes((int) toRefill);
        FREE_HASHES_CACHE.addAll(dbHashes);
        log.info("Added {} hashes from DB to cache", dbHashes.size());    }

    @Async("hashServiceExecutor")
    public void refillDbIfNeeded(long capacity) {
        freeHashGenerator.refillDb(capacity);
    }

    @Transactional
    public void warmUpCache() {
        long freeHashesInDb = freeHashRepository.count();
        if (freeHashesInDb < maxDbCapacity) {
            long refillDatabaseCount = maxDbCapacity - freeHashesInDb;
            freeHashGenerator.refillDb(refillDatabaseCount);
        }
        List<FreeHash> dbHashes = freeHashRepository.deleteAndReturnFreeHashes((int) maxCacheCapacity);
        FREE_HASHES_CACHE.addAll(dbHashes);
    }
}
