package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.CacheProperties;
import faang.school.urlshortenerservice.config.DatabaseProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.HashRetrievalException;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final CacheProperties cacheProperties;
    private final DatabaseProperties databaseProperties;
    private final AtomicBoolean cacheUpdateInProgress = new AtomicBoolean(false);
    private final AtomicBoolean dbUpdateInProgress = new AtomicBoolean(false);

    private BlockingQueue<String> cache;

    @PostConstruct
    public void init() {
        this.cache = new ArrayBlockingQueue<>(cacheProperties.maxCacheSize());
        cacheWarmUp();
    }

    public String takeCache() {
        try {
            updateCacheIfNeeded();
            String hash = cache.take();
            log.info("Hash '{}' was taken from cache", hash);
            return hash;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HashRetrievalException("Unable to retrieve hash");
        }
    }

    private void putCache() {
        cache.addAll(hashRepository.popUrlHashes((long) (cacheProperties.maxCacheSize()
                                        * (cacheProperties.cacheUpdateBatchPercentage() / 100.0)))
                .stream()
                .map(Hash::getHash)
                .toList()
        );
        log.info("Cache was updated, cache size: {}", cache.size());
    }

    private void updateCacheIfNeeded() {
        int cacheSize = cache.size();
        log.info("Checking if cache needs to be updated, cache size: {}", cacheSize);
        updateHashInDatabaseIfNeeded();
        if (cacheSize <= cacheProperties.maxCacheSize() * (cacheProperties.cacheUpdateThresholdPercentage() / 100)
                && cacheUpdateInProgress.compareAndSet(false, true)) {
            log.info("Cache update triggered. Updating cache in background thread");
            try {
                threadPoolTaskExecutor.execute(() -> {
                    try {
                        putCache();
                    } finally {
                        cacheUpdateInProgress.set(false);
                    }
                });
            } catch (Exception e) {
                log.error("Failed to submit task to thread pool", e);
                cacheUpdateInProgress.set(false);
            }
        }
    }

    private void updateHashInDatabaseIfNeeded() {
        long count = hashRepository.count();
        log.info("Checking if database needs to be updated, count: {}", count);
        if (count <= databaseProperties.minHashesInDatabase() && dbUpdateInProgress.compareAndSet(false, true)) {
            log.info("Database update triggered. Generating hashes in background thread");
            threadPoolTaskExecutor.execute(() -> {
                try {
                    hashGenerator.generateAndSaveHashes();
                } finally {
                    dbUpdateInProgress.set(false);
                }
            });
        }
    }

    private void cacheWarmUp() {
        if (hashRepository.count() <= cacheProperties.maxCacheSize()) {
            log.info("Database is not yet warmed up. Generating hashes");
            hashGenerator.generateAndSaveHashes();
        }
        putCache();
        log.info("Cache and database was warmed up!");
    }
}
