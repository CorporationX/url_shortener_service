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

    @Value("${shortener.hash-pool.max-capacity}")
    private long maxCapacity;

    @Value("${shortener.hash-pool.refill-threshold-percent}")
    private int refillThresholdPercent;

    public FreeHash getAvailableHash() {
        triggerAsyncRefillIfNeeded();
        return FREE_HASHES_CACHE.poll();
    }

    private void triggerAsyncRefillIfNeeded() {
        long threshold = getThreshold();
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
            long toRefill = maxCapacity - availableHashes;
            applicationContext.getBean(HashService.class).refill(toRefill);
        } finally {
            lock.set(false);
        }
    }

    @Transactional
    public void warmUpCache() {
        refill(maxCapacity);
    }

    @Transactional
    public void refill(long toRefill) {
        long freeHashesInDb = freeHashRepository.count();
        if (freeHashesInDb >= toRefill) {
            List<FreeHash> dbHashes = freeHashRepository.findAndLockFreeHashes((int) toRefill);
            FREE_HASHES_CACHE.addAll(dbHashes);

            freeHashRepository.deleteAllByIdInBatch(
                    dbHashes.stream().map(FreeHash::getHash).toList()
            );

            log.info("Added {} hashes from DB to cache", dbHashes.size());
        } else {
            List<Long> range = freeHashRepository.generateBatch(toRefill);
            List<FreeHash> generatedHashes = freeHashGenerator.generateHashes(range);
            FREE_HASHES_CACHE.addAll(generatedHashes);

            log.info("Added {} hashes from generator to cache", generatedHashes.size());
        }
    }

    private long getThreshold() {
        return maxCapacity * refillThresholdPercent / 100;
    }
}
