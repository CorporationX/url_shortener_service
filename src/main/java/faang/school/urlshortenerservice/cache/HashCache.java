package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.UrlShortenerProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exeption.LocalCacheException;
import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final ArrayBlockingQueue<Hash> localCache;
    private final HashService hashService;
    private final ThreadPoolTaskExecutor localCacheExecutor;
    private final UrlShortenerProperties urlShortenerProperties;
    private final AtomicBoolean uploadInProgressFlag = new AtomicBoolean(false);
    private long lowerBoundCapacity;

    @PostConstruct
    public void init() {
        this.lowerBoundCapacity = calculateLowerBoundCapacity();
        log.info("Initialized local cache lower bound: {}", lowerBoundCapacity);
    }

    public String getHashFromCache() {
        addHashToLocalCacheIfNecessary();

        Hash hash = Optional.ofNullable(localCache.poll())
                .orElseThrow(this::createLocalCacheException);
        return hash.getHash();
    }

    public void addHashToLocalCacheIfNecessary() {
        boolean needRefill = localCache.size() < lowerBoundCapacity
                || localCache.isEmpty();

        if (needRefill && uploadInProgressFlag.compareAndSet(false, true)) {
            log.info("Cache refill triggered. Current size: {}", localCache.size());
            startCacheRefill();
        }
    }

    private void startCacheRefill() {
        localCacheExecutor.execute(() -> {
            try {
                List<Hash> hashes = getHashesFromDatabaseAndWaitUntilDone();
                localCache.addAll(hashes);
                log.info("Refilled {} hashes. New cache size: {}",
                        hashes.size(),
                        localCache.size());
                hashService.uploadHashInDatabaseIfNecessary();
            } catch (Exception e) {
                log.error("Cache refill failed", e);
            } finally {
                uploadInProgressFlag.set(false);
            }
        });
    }

    public void uploadHashFromDatabaseToLocalCache() {
        List<Hash> hashes = getHashesFromDatabaseAndWaitUntilDone();
        localCache.addAll(hashes);
        log.info("{} hashes added to local cache successfully", hashes.size());
        hashService.uploadHashInDatabaseIfNecessary();
    }

    public List<Hash> getHashesFromDatabaseAndWaitUntilDone() {
        try {
            return hashService.getHashesFromDatabase().get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error during hash download from database. Error: {}", e.getMessage());
            Thread.currentThread().interrupt();
            throw new IllegalStateException(String.format(
                    "Error during hash download from database. Error: %s", e.getMessage()), e);
        }
    }

    private long calculateLowerBoundCapacity() {
        return (long) (urlShortenerProperties.hashAmountToLocalCache()
                * urlShortenerProperties.localCacheThresholdRatio());
    }

    private LocalCacheException createLocalCacheException() {
        String errorMessage = "Unable to provide hash for short URL";
        log.error(errorMessage);
        return new LocalCacheException(errorMessage);
    }

    public void warmupCache() {
        if (uploadInProgressFlag.compareAndSet(false, true)) {
            try {
                List<Hash> hashes = hashService.generateBatch(
                        urlShortenerProperties.hashAmountToLocalCache()
                );
                localCache.addAll(hashes);
                hashService.uploadHashInDatabaseIfNecessary();
                log.info("Warmup completed. Initial cache size: {}", localCache.size());
            } finally {
                uploadInProgressFlag.set(false);
            }
        }
    }
}

