package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.property.HashCacheProperty;
import faang.school.urlshortenerservice.service.hash.api.HashCache;
import faang.school.urlshortenerservice.service.hash.api.HashGenerator;
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

    @Override
    public String getHash() {
        try {
            ensureCacheIsFilled();
            return hashes.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread interrupted while retrieving hash", e);
        }
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

        double threshold = hashCacheProperties.getSize() * (hashCacheProperties.getLowPercent() / 100.0);

        return hashes.size() < threshold
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

    private int getBatchSizeForCache() { return hashCacheProperties.getSize(); }

    private void ensureSufficientHashes() {
        if (hashGenerator.isMinimumThresholdExceeded()) {
            hashGenerator.generateHashes();
        }
    }
}
