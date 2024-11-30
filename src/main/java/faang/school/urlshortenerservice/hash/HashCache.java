package faang.school.urlshortenerservice.hash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class HashCache {
    private final int maxCacheSize;
    private final double thresholdSize;
    private final ThreadPoolTaskExecutor executor;
    private final HashFiller hashFiller;
    private final BlockingQueue<String> queueHash;

    public HashCache(
        @Value("${hash-cache.max-cache-size}") int maxCacheSize,
        @Value("${hash-cache.threshold-fraction-size}") double thresholdFractionSize,
        @Qualifier("hashCacheExecutor") ThreadPoolTaskExecutor executor,
        HashFiller hashFiller,
        BlockingQueue<String> queueHash
    ) {
        this.maxCacheSize = maxCacheSize;
        this.executor = executor;
        this.hashFiller = hashFiller;
        this.queueHash = queueHash;
        this.thresholdSize = maxCacheSize * thresholdFractionSize;
    }

    public String getHash() {
        executor.execute(this::checkAndFillHashCache);

        try {
            String hash = queueHash.poll(3, TimeUnit.SECONDS);
            while (hash == null) {
                hash = queueHash.poll(3, TimeUnit.SECONDS);
            }
            return hash;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void checkAndFillHashCache() {
        if (queueHash.size() < thresholdSize) {
            int batchSize = maxCacheSize - queueHash.size();
            log.info("Starting adding {} hashes to the queue", batchSize);
            hashFiller.fillHashCache(batchSize)
                .thenAccept(queueHash::addAll);
        }
    }
}