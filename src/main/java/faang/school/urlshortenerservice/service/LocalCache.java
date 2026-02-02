package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class LocalCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService hashCacheExecutor;

    @Value("${hash.cache.size}")
    private int cacheSize;

    @Value("${hash.cache.refill-threshold-percent}")
    private int refillThresholdPercent;

    @Value("${hash.repository.fetch-batch-size}")
    private int fetchBatchSize;

    private ArrayBlockingQueue<String> cache;

    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    @PostConstruct
    void init() {
        this.cache = new ArrayBlockingQueue<>(cacheSize);
        hashGenerator.generateBatchSyncForBootstrap();
        refillCache();
    }

    public String getHash() {
        String hash = cache.poll();

        if (hash == null) {
            throw new IllegalStateException("Hash cache is empty");
        }

        checkAndRefillAsync();
        return hash;
    }

    private void checkAndRefillAsync() {
        int threshold = calculateThresholdPercent();

        if (cache.size() > threshold) {
            return;
        }

        if (!refillInProgress.compareAndSet(false, true)) {
            return;
        }

        try {
            hashCacheExecutor.submit(() -> {
                try {
                    refillCache();
                    hashGenerator.generateBatchAsync();
                } finally {
                    refillInProgress.set(false);
                }
            });
        } catch (RuntimeException e) {
            refillInProgress.set(false);
            throw e;
        }

    }

    /*
    Invariant:
    refill is triggered only when cache.size() < threshold,
    and fetchBatchSize <= (cacheSize - threshold).
    Therefore, the queue should never overflow in current configuration.

    offer() result is still checked defensively to prevent silent data loss
    in case config values change or invariants are broken in the future.
     */
    private void refillCache() {
        List<String> hashes =
                hashRepository.getAndDeleteHashes(fetchBatchSize);

        for (String hash : hashes) {
            if (!cache.offer(hash)) {
                break;
            }
        }
    }

    private int calculateThresholdPercent() {
        return cacheSize * refillThresholdPercent / 100;
    }
}