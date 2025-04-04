package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
@Slf4j
@Service
public class HashCache {
    private final HashRepository hashRepository;
    private final ExecutorService executorService;
    private final HashGeneratorService hashGeneratorService;
    private final RedisLockRegistry redisLockRegistry;

    @Value("${cache.hash.size:1000}")
    private int maxCacheSize;

    @Value("${cache.hash.threshold-percent:20}")
    private int thresholdPercent;

    @Value("${cache.hash.batch-size:500}")
    private int batchSize;

    // Minimum hashes that should be available in DB before generating new ones
    @Value("${cache.hash.db.min-available:1000}")
    private int minDbAvailable;

    private final ConcurrentLinkedQueue<String> hashQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        refillCache();
        if (hashQueue.size() < maxCacheSize / 2) {
            refillCache();
        }
    }

    public String getHash() {
        checkAndRefillIfNeeded();
        return hashQueue.poll();
    }

    public List<String> getHashCache(List<Long> numbers) {
        checkAndRefillIfNeeded();

        int requestCount = numbers.size();
        if (requestCount == 0) return List.of();
        if (requestCount == 1) return List.of(Objects.requireNonNull(hashQueue.poll()));

        int size = Math.min(numbers.size(), hashQueue.size());
        List<String> result = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            result.add(hashQueue.poll());
        }

        return result;
    }

    private void checkAndRefillIfNeeded() {
        int currentSize = hashQueue.size();
        int threshold = maxCacheSize * thresholdPercent / 100;

        if (currentSize < threshold && isRefilling.compareAndSet(false, true)) {
            log.info("Cache below threshold ({}%). Current size: {}. Starting async refill.",
                    thresholdPercent, currentSize);
            executorService.submit(this::refillCache);
        }
    }

    private void refillCache() {
        try {
            log.info("Starting cache refill. Current size: {}", hashQueue.size());
            fetchExistingHashes();
            checkAndGenerateNewHashes();
            log.info("Cache refill completed. New size: {}", hashQueue.size());
        } catch (Exception e) {
            log.error("Error during hash cache refill", e);
        } finally {
            isRefilling.set(false);
        }
    }

    private void fetchExistingHashes() {
        List<String> existingHashes = hashRepository.getAvailableHashes(batchSize);
        if (!existingHashes.isEmpty()) {
            log.info("Got {} existing hashes from database", existingHashes.size());
            hashQueue.addAll(existingHashes);
        }
    }

    private void checkAndGenerateNewHashes() {
        int dbAvailableCount = hashGeneratorService.getAvailableHashesCount();
        if (dbAvailableCount < minDbAvailable) {
            log.info("DB hash count ({}) below minimum ({}). Acquiring distributed lock for generation.",
                    dbAvailableCount, minDbAvailable);
            generateHashesWithLock();
        }
    }

    private void generateHashesWithLock() {
        Lock lock = redisLockRegistry.obtain("hash-generation-lock");
        boolean lockAcquired = false;

        try {
            lockAcquired = acquireLockWithRetry(lock);
            if (lockAcquired) {
                generateAndFetchAdditionalHashes();
            } else {
                log.info("Could not acquire lock for hash generation, another instance is likely handling it");
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while trying to acquire hash generation lock", e);
            Thread.currentThread().interrupt();
        } finally {
            if (lockAcquired) {
                lock.unlock();
                log.info("Released distributed lock for hash generation");
            }
        }
    }

    private boolean acquireLockWithRetry(Lock lock) throws InterruptedException {
        int retries = 3;
        while (retries > 0) {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                return true;
            }
            retries--;
            if (retries > 0) {
                log.info("Failed to acquire lock, retrying... ({} attempts left)", retries);
                Thread.sleep(1000);
            }
        }
        return false;
    }

    private void generateAndFetchAdditionalHashes() {
        int currentDbCount = hashGeneratorService.getAvailableHashesCount();
        if (currentDbCount < minDbAvailable) {
            hashGeneratorService.generateBatch();
        }

        if (hashQueue.size() < maxCacheSize / 2) {
            fetchAdditionalHashes();
        }
    }

    private void fetchAdditionalHashes() {
        int existingHashCount = hashQueue.size();
        List<String> newHashes = hashRepository.getAvailableHashes(batchSize - existingHashCount);
        if (!newHashes.isEmpty()) {
            log.info("Got {} additional hashes after generation", newHashes.size());
            hashQueue.addAll(newHashes);
        }
    }
}
