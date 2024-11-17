package faang.school.urlshortenerservice.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash-cache.max-cache-size}")
    private final int maxCacheSize;

    @Value("${hash-cache.threshold-fraction-size}")
    private final double thresholdFractionSize;
    private final BlockingQueue<String> queueHash;

    @Qualifier("hashCacheExecutor")
    private final ThreadPoolTaskExecutor executor;
    private final HashGenerator hashGenerator;

    @Transactional
    public String getHash() {
        executor.execute(this::checkAndFillHashCache);

        try {
            String hash = queueHash.poll(5, TimeUnit.SECONDS);
            if (hash == null) {
                throw new RuntimeException("There are no short links available");
            }
            return hash;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void checkAndFillHashCache() {
        if (queueHash.size() < maxCacheSize * thresholdFractionSize) {
            int batchSize = maxCacheSize - queueHash.size();
            log.info("Starting adding {} hashes to the queue", batchSize);
            hashGenerator.fillHashCache(queueHash, batchSize);
        }
    }
}