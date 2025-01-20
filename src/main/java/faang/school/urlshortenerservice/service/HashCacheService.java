package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.UrlShortenerProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.LocalCacheException;
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
public class HashCacheService {
    private final ArrayBlockingQueue<Hash> localCache;
    private final HashService hashService;
    private final ThreadPoolTaskExecutor localCacheExecutor;
    private final UrlShortenerProperties urlShortenerProperties;
    private final AtomicBoolean uploadInProgressFlag = new AtomicBoolean();

    public String getHashFromCache() {
        Hash hash = Optional.ofNullable(localCache.poll()).orElseThrow(this::createLocalCacheException);

        addHashToLocalCacheIfNecessary();
        return hash.getHash();
    }

    public void addHashToLocalCacheIfNecessary() {
        if (!isEnoughLocalCacheCapacity() && uploadInProgressFlag.compareAndSet(false, true)) {
            log.info("{} hashes left in local cache (threshold is {}). Update started", localCache.size(), (long) (urlShortenerProperties.hashAmountToLocalCache() * urlShortenerProperties.localCacheThresholdRatio()));
            localCacheExecutor.execute(() -> {
                try {
                    uploadHashFromDatabaseToLocalCache();
                } finally {
                    uploadInProgressFlag.set(false);
                }
            });
        }
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

    private boolean isEnoughLocalCacheCapacity() {
        long lowerBoundCapacity = (long) (urlShortenerProperties.hashAmountToLocalCache() * urlShortenerProperties.localCacheThresholdRatio());
        return localCache.size() >= lowerBoundCapacity;
    }

    private LocalCacheException createLocalCacheException() {
        String errorMessage = "Unable to provide hash for short URL";
        log.error(errorMessage);
        return new LocalCacheException(errorMessage);
    }

}