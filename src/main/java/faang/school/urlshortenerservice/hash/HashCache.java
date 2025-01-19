package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.properties.HashLocalCacheProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCache {
    private final HashGenerator hashGenerator;
    private final BlockingQueue<String> hashCache;
    private final AtomicBoolean inProgress = new AtomicBoolean(false);
    private final HashLocalCacheProperties hashLocalCacheProperties;

    public HashCache(HashGenerator hashGenerator, HashLocalCacheProperties hashLocalCacheProperties) {
        this.hashGenerator = hashGenerator;
        this.hashLocalCacheProperties = hashLocalCacheProperties;
        this.hashCache = new LinkedBlockingQueue<>(hashLocalCacheProperties.getSize());
    }

    @PostConstruct
    public void init() {
        log.info("Initializing HashCache...");
        if (hashGenerator.isBelowMinimum()) {
            log.warn("Not enough hashes in database");
            hashGenerator.generateHashBatch();
        }
        fillHashCache();
        log.info("HashCache initialized successfully. Current cache size: {}", hashCache.size());
    }

    public String getHash() {
        return hashCache.poll();
    }

    @Async("hashCacheFillExecutor")
    public void checkAndFillHashCache() {
        if (checkFilling()) {
            fillHashCache();
        }
    }

    private boolean checkFilling() {
        if (inProgress.get()) {
            return false;
        }
        return hashCache.size() < (int) (hashLocalCacheProperties.getSize() * (hashLocalCacheProperties.getLowPercent() / 100))
                && inProgress.compareAndSet(false, true);
    }

    private void fillHashCache() {
        try {
            log.info("Filling hash cache");
            hashCache.addAll(hashGenerator.getHashes(getBatchSizeForCache()));
            log.info("Hash cache filled. Current size: {}", hashCache.size());
            checkAndGenerateHashes();
        } finally {
            inProgress.set(false);
        }
    }

    private int getBatchSizeForCache() {
        return Math.min(hashLocalCacheProperties.getSize() - hashCache.size(), 1000);
    }

    private void checkAndGenerateHashes() {
        if (hashGenerator.isBelowMinimum()) {
            log.warn("Not enough hashes in database");
            hashGenerator.generateHashBatchAsync();
        }
    }
}
