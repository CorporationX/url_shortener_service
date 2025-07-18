package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.exception.HashRetrievalTimeoutException;
import faang.school.urlshortenerservice.repository.HashDao;
import faang.school.urlshortenerservice.schedule.HashDbMaintainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class InMemoryLocalCache implements LocalCache {
    private final HashDao hashDa;
    private final Executor taskExecutor;
    private final AtomicBoolean isReplenishing = new AtomicBoolean(false);

    private final HashDbMaintainer hashDbMaintainer;

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.low-cache-mark-percentage}")
    private int lowCacheMarkPercentage;

    @Value("${hash.cache.poll-timeout-ms}")
    private int pollTimeoutMs;

    @Value("${hash.cache.startup-timeout-seconds}")
    private long startupTimeoutSeconds;

    private BlockingQueue<String> hashQueue;

    public InMemoryLocalCache(HashDao hashDao,
                              @Qualifier("taskExecutor") Executor taskExecutor,
                              HashDbMaintainer hashDbMaintainer) {
        this.hashDa = hashDao;
        this.taskExecutor = taskExecutor;
        this.hashDbMaintainer = hashDbMaintainer;
    }


    @Override
    @EventListener(ApplicationReadyEvent.class)
    public void initializeCache() {
        hashQueue = new LinkedBlockingQueue<>(capacity);
        log.info("Initialized in-memory hash cache with capacity: {}", capacity);
        log.info("Starting cache warm-up. This will block startup until complete or timeout is reached ({}s).",
                startupTimeoutSeconds);
        try {
            hashDbMaintainer.replenishDbHashes();
            refillQueueFromDb();
            log.info("Cache warm-up successful. Initial queue size: {}", hashQueue.size());
        } catch (Exception e) {
            log.error("An error occurred during cache warm-up.", e);
        }
    }

    @Override
    public String getHash() {
        try {
            String hash = hashQueue.poll(pollTimeoutMs, TimeUnit.MILLISECONDS);
            if (hash == null) {
                log.warn("Could not retrieve a hash from the cache within {} ms..", pollTimeoutMs);
                throw new HashRetrievalTimeoutException();
            }
            checkAndTriggerRefill();
            return hash;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted while waiting for a hash.", e);
            throw new RuntimeException("Interrupted while waiting for a hash", e);
        }
    }

    private void checkAndTriggerRefill() {
        int lowWaterMark = capacity * lowCacheMarkPercentage / 100;
        if (hashQueue.size() <= lowWaterMark
                && isReplenishing.compareAndSet(false, true)) {
            log.info("Hash cache size ({}) is below the low-water mark ({}). Triggering async refill.",
                    hashQueue.size(), lowWaterMark);
            CompletableFuture.runAsync(this::refillQueueFromDb, taskExecutor)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            log.error("The async refill task submission failed.", throwable);
                        }
                        isReplenishing.set(false);
                        log.info("Queue replenishment task finished. Lock released.");
                    });
        }
    }

    private void refillQueueFromDb() {
        int hashesNeeded = capacity - hashQueue.size();
        if (hashesNeeded <= 0) {
            log.warn("No hashes needed to be refilled. Skipping refill.");
            return;
        }
        log.info("Attempting to refill queue by fetching {} hashes from the database", hashesNeeded);
        List<String> hashesFromDb = hashDa.getHashBatch(hashesNeeded);
        if (!hashesFromDb.isEmpty()) {
            hashQueue.addAll(hashesFromDb);
            log.info("Successfully added {} hashes to the queue. New queue size: {}",
                    hashesFromDb.size(), hashQueue.size());
        } else {
            log.warn("Could not refill queue. No hashes available in the database.");
        }
    }
}
