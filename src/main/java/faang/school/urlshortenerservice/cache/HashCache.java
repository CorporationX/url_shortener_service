package faang.school.urlshortenerservice.cache;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashJdbcRepository hashJdbcRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService hashCacheExecutor;

    @Value("${hash.cache-refill.size}")
    private int maxSize;

    @Value("${hash.cache-refill.threshold-percent}")
    private int refillThresholdPercent;

    private final BlockingQueue<String> cache = new LinkedBlockingQueue<>();
    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    public String getHash() {
        checkAndTriggerRefill();

        try {
            String hash = cache.poll(30, TimeUnit.SECONDS);
            if (hash == null) {
                throw new IllegalStateException("Hash cache is empty. Please try again later.");
            }
            return hash;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for hash from cache", e);
        }
    }

    private void checkAndTriggerRefill() {
        int threshold = maxSize * refillThresholdPercent / 100;

        if (cache.size() > threshold) {
            return;
        }

        if (refillInProgress.compareAndSet(false, true)) {
            log.info("Hash cache below threshold ({}), triggering refill", cache.size());

            hashCacheExecutor.submit(() -> {
                try {
                    refillCache();
                } finally {
                    refillInProgress.set(false);
                }
            });
        }
    }

    private void refillCache() {
        log.info("Refilling hash cache");

        List<String> hashes = hashJdbcRepository.getHashBatch();
        cache.addAll(hashes);

        hashGenerator.generateBatch();

        log.info("Refilled hash cache with {} hashes", hashes.size());
    }
}
