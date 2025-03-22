package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.property.HashCacheProperty;
import jakarta.annotation.PostConstruct;
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
    private final HashCacheProperty hashLocalCacheProperties;

    public HashCache(HashGenerator hashGenerator, HashCacheProperty hashLocalCacheProperties) {
        this.hashGenerator = hashGenerator;
        this.hashLocalCacheProperties = hashLocalCacheProperties;
        this.hashCache = new LinkedBlockingQueue<>(hashLocalCacheProperties.getSize());
    }

    @PostConstruct
    public void initializing() {
        log.info("Initializing Cache");
        ensureSufficientHashes();
        fillCache();
        log.info("Cache initialized successfully");
    }

    public String getHash() {
        return hashCache.poll();
    }

    @Async("hashCacheFillExecutor")
    public void ensureCacheIsFilled() {
        if (isFilling()) {
            fillCache();
        }
    }

    private boolean isFilling() {
        if (inProgress.get()) {
            return false;
        }

        return hashCache.size() < (hashLocalCacheProperties.getSize() * (hashLocalCacheProperties.getLowPercent() / 100))
                && inProgress.compareAndSet(false, true);
    }

    private void fillCache() {
        try {
            log.info("Filling hash cache");
            hashCache.addAll(hashGenerator.getHashes(getBatchSizeForCache()));
            log.info("Hash cache filled. Current size: {}", hashCache.size());
            ensureSufficientHashes();
        } finally {
            inProgress.set(false);
        }
    }

    private int getBatchSizeForCache() {
        return Math.min(hashLocalCacheProperties.getSize() - hashCache.size(), 100);
    }

    private void ensureSufficientHashes() {
        if (hashGenerator.isMinimumThresholdExceeded()) {
            log.warn("No free hashes in the DB. generating new ones...");
            hashGenerator.generateHashes();
            log.warn("Hashes created.");
        }
    }
}
