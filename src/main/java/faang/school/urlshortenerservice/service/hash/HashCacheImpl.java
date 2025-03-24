package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.property.HashCacheProperty;
import faang.school.urlshortenerservice.service.hash.api.HashCache;
import faang.school.urlshortenerservice.service.hash.api.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCacheImpl implements HashCache {
    private final HashGenerator hashGenerator;
    private final BlockingQueue<String> hashes;
    private final AtomicBoolean isGeneratingHashes = new AtomicBoolean(false);
    private final HashCacheProperty hashCacheProperties;

    public HashCacheImpl(HashGeneratorImpl hashGenerator, HashCacheProperty hashCacheProperties) {
        this.hashGenerator = hashGenerator;
        this.hashCacheProperties = hashCacheProperties;
        this.hashes = new LinkedBlockingQueue<>(hashCacheProperties.getSize());
    }

    @PostConstruct
    @Override
    public void initializing() {
        log.info("Initializing Cache");
        ensureSufficientHashes();
        fillCache();
        log.info("Cache initialized successfully");
    }

    @Override
    public String getHash() {
        return hashes.poll();
    }

    @Async("hashCacheFillExecutor")
    @Override
    public void ensureCacheIsFilled() {
        if (needsFilling()) {
            fillCache();
        }
    }

    private boolean needsFilling() {
        if (isGeneratingHashes.get()) {
            return false;
        }

        return hashes.size() < (hashCacheProperties.getSize() * (hashCacheProperties.getLowPercent() / 100))
                && isGeneratingHashes.compareAndSet(false, true);
    }

    private void fillCache() {
        try {
            log.info("Filling hash cache");
            hashes.addAll(hashGenerator.getHashes(getBatchSizeForCache()));
            log.info("Hash cache filled. Current size: {}", hashes.size());
            ensureSufficientHashes();
        } finally {
            isGeneratingHashes.set(false);
        }
    }

    private int getBatchSizeForCache() {
        return Math.min(hashCacheProperties.getSize() - hashes.size(), 100);
    }

    private void ensureSufficientHashes() {
        if (hashGenerator.isMinimumThresholdExceeded()) {
            hashGenerator.generateHashes();
        }
    }
}
