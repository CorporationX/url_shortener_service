package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Slf4j
@Service
public class HashCache {
    private final HashRepository hashRepository;
    private final ExecutorService executorService;
    private final HashGenerator hashGenerator;
    private final JdbcTemplate jdbcTemplate;

    private final ConcurrentLinkedQueue<String> hashQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @Value("${cache.hash.size:1000}")
    private int maxCacheSize;

    @Value("${cache.hash.threshold-percent:20}")
    private int thresholdPercent;

    @Value("${cache.hash.batch-size:500}")
    private int batchSize;

    @Value("${cache.hash.db-threshold:100}")
    private int dbThreshold;

    @PostConstruct
    public void init() {
        refillCache();
    }

    public String getHash() {
        checkAndRefillIfNeeded();
        return hashQueue.poll();
    }

    public String getNextHash() {
        checkAndRefillIfNeeded();
        return hashQueue.poll();
    }

    private void checkAndRefillIfNeeded() {
        int threshold = maxCacheSize * thresholdPercent / 100;
        if (hashQueue.size() < threshold && isRefilling.compareAndSet(false, true)) {
            log.info("Cache below threshold ({}%). Starting async refill.", thresholdPercent);
            executorService.submit(this::refillCache);
        }
    }

    private void refillCache() {
        try {
            log.info("Starting cache refill. Current size: {}", hashQueue.size());
            List<Long> availableHashes = hashRepository.getUniqueNumbers(batchSize);
            if (availableHashes != null && !availableHashes.isEmpty()) {
                availableHashes.forEach(number -> hashQueue.offer(number.toString()));
                log.info("Refilled cache from DB with {} hashes.", availableHashes.size());
            } else {
                log.info("No available hashes in DB.");
            }
            int dbCount = hashRepository.countAvailableHashes();
            if (dbCount < dbThreshold) {
                log.info("DB hash count ({}) is below threshold ({}).", dbCount, dbThreshold);
                 if (acquireDistributedLock()) {
                    try {
                        log.info("Distributed lock acquired. Generating new batch via hashGenerator.");
                        generateBatchWithLock();
                    } finally {
                        releaseDistributedLock();
                    }
                } else {
                    log.info("Another instance is already generating new hashes.");
                }
            }
            log.info("Cache refill completed. New cache size: {}", hashQueue.size());

        } catch (
                Exception e) {
            log.error("Error during hash cache refill", e);
        } finally {
            isRefilling.set(false);
        }
    }

    private boolean acquireDistributedLock() {
        try {
            jdbcTemplate.execute("SELECT pg_advisory_lock(hashtext('hash_table_batch_operation')::bigint)");
            return true;
        } catch (DataAccessException e) {
            log.error("Failed to acquire advisory lock", e);
            return false;
        }
    }

    private void releaseDistributedLock() {
        try {
            jdbcTemplate.execute("SELECT pg_advisory_unlock(hashtext('hash_table_batch_operation')::bigint)");
        } catch (DataAccessException e) {
            log.error("Failed to release advisory lock", e);
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void generateBatchWithLock() {
         hashGenerator.generateBatch();
    }
}


