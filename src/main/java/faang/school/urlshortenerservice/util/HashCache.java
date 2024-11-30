package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.HashService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCache {
    private final HashService hashService;
    private final HashGenerator hashGenerator;
    private final HashProperties hashProperties;
    private final ThreadPoolTaskExecutor taskExecutor;

    private final AtomicBoolean isFilling;
    private final double lowCacheThreshold;
    private final BlockingQueue<Hash> blockingQueue;

    public HashCache(HashService hashService, HashGenerator hashGenerator,
                     HashProperties hashProperties, ThreadPoolTaskExecutor taskExecutor) {
        this.hashService = hashService;
        this.hashGenerator = hashGenerator;
        this.hashProperties = hashProperties;
        this.taskExecutor = taskExecutor;

        this.isFilling = new AtomicBoolean(false);
        this.lowCacheThreshold = hashProperties.getLowCacheThreshold();
        this.blockingQueue = new LinkedBlockingQueue<>(hashProperties.getCapacity());

        log.info("Initializing hash cache with capacity: {} and low cache threshold: {}.",
                hashProperties.getCapacity(), lowCacheThreshold
        );

        refillHashCache();
    }

    public Hash getHash() {
        if (blockingQueue.size() < lowCacheThreshold && !isFilling.get()) {
            log.warn("Hash cache size dropped below {}%, triggering refill.", hashProperties.getLowSizePercentage());
            refillHashCache();
        }
        return blockingQueue.poll();
    }

    private void refillHashCache() {
        if (!isFilling.compareAndSet(false, true)) {
            return;
        }
        taskExecutor.execute(this::asyncFillCache);
    }

    private void asyncFillCache() {
        try {
            log.info("Refilling hash cache started.");
            hashGenerator.generateBatch();
            List<Hash> hashBatch = hashService.getHashBatch();
            blockingQueue.addAll(hashBatch);
        } catch (Exception e) {
            log.error("Hash cache refill error: {}", e.getMessage(), e);
        } finally {
            isFilling.set(false);
        }
    }
}
