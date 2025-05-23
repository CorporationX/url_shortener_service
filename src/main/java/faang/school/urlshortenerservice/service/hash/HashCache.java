package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.config.hash.HashCacheConfig;
import faang.school.urlshortenerservice.exception.HashUnavailableException;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private static final int PERCENTAGE_MAX_VALUE = 100;
    private final AtomicBoolean isCacheBeingRefilled = new AtomicBoolean(false);
    private final HashCacheConfig hashCacheConfig;
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    @Qualifier("hashRefillExecutor")
    private final ExecutorService hashRefillExecutor;
    @Qualifier("availableHashesQueue")
    private final BlockingQueue<String> availableHashes;
    private int cacheRefillThreshold;

    @PostConstruct
    public void init() {
        this.cacheRefillThreshold = computeCacheRefillThreshold();
    }

    public String getHash() {
        try {
            if (needRefill()) {
                refillCache();
            }
            return availableHashes.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HashUnavailableException("Hash generation interrupted", e);
        }
    }

    private boolean needRefill() {
        return availableHashes.size() < cacheRefillThreshold;
    }

    private void refillCache() {
        if (isCacheBeingRefilled.compareAndSet(false, true)) {
            hashRefillExecutor.submit(() -> {
                try {
                    log.info("Starting to refill the cache");
                    List<String> hashes = hashRepository.getAndDeleteHashBatch();
                    availableHashes.addAll(hashes);
                    log.info("Finished refilling with {} elements", hashes.size());
                    hashGenerator.generateHashesBatch();
                } finally {
                    isCacheBeingRefilled.set(false);
                }
            });
        }
    }

    private int computeCacheRefillThreshold() {
        int maxSize = hashCacheConfig.getMaxSize();
        int minPercent = hashCacheConfig.getRefillThresholdPercent();
        return (maxSize * minPercent) / PERCENTAGE_MAX_VALUE;
    }
}