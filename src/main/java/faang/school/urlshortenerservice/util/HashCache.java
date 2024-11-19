package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashService hashService;
    private final HashGenerator hashGenerator;
    private final HashProperties hashProperties;
    private final ThreadPoolTaskExecutor taskExecutor;

    private double lowCacheThreshold;
    private AtomicBoolean isFilling;
    private BlockingQueue<Hash> blockingQueue;


    @PostConstruct
    private void setUp() {
        lowCacheThreshold = hashProperties.getLowCacheThreshold();
        isFilling = new AtomicBoolean(false);
        blockingQueue = new ArrayBlockingQueue<>(hashProperties.getCapacity(), true);

        log.info("Initializing hash cache with capacity: {} and low cache threshold: {}.",
                hashProperties.getCapacity(), lowCacheThreshold
        );

        refillHashCache();
    }

    public Hash getHash() {
        refillTrigger();
        return blockingQueue.poll();
    }

    private void refillTrigger() {
        if (blockingQueue.size() < lowCacheThreshold && !isFilling.get()) {
            log.warn("Hash cache size dropped below {}%, triggering refill.", hashProperties.getLowSizePercentage());
            refillHashCache();
        }
    }

    private void refillHashCache() {
        if (!isFilling.compareAndSet(false, true)) {
            return;
        }
        taskExecutor.execute(this::fillCache);
    }

    private void fillCache() {
        try {
            log.info("Refilling hash cache started.");
            hashGenerator.generateBatch();
            List<Hash> hashBatch = hashService.getHashBatch();

            int addedHashCount = 0;
            for (Hash hash : hashBatch) {
                if (!blockingQueue.offer(hash)) {
                    log.warn("Hash cache reached its capacity. Skipped remaining hashes.");
                    break;
                }
                addedHashCount++;
            }

            log.info("Refilling hash cache completed. Added {} hashes. Current size: {}",
                    addedHashCount, blockingQueue.size()
            );
        } catch (Exception e) {
            log.error("Hash cache refill error: {}", e.getMessage(), e);
        } finally {
            isFilling.set(false);
        }
    }
}
