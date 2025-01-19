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
    private final AtomicBoolean hashUpdateInProgress = new AtomicBoolean(false);
    private final AtomicBoolean dbUpdateInProgress = new AtomicBoolean(false);

    private BlockingQueue<String> cache;

    @PostConstruct
    public void init() {
        this.cache = new ArrayBlockingQueue<>(cacheProperties.size());
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
        cache.addAll(hashRepository.popUrlHashes((long) (cacheProperties.size() * cacheProperties.percentSizeToUpdate()))
                .stream()
                .map(Hash::getHash)
                .toList()
        );
        log.info("Cache was updated, cache size: {}", cache.size());
    }

    private void updateCacheIfNeeded() {
        int cacheSize = cache.size();
        log.info("Checking if cache needs to be updated, cache size: {}", cacheSize);
        generateHashInDatabaseIfNeeded();
        if (cacheSize <= cacheProperties.size() * cacheProperties.percentSizeToTriggerUpdate()
                && hashUpdateInProgress.compareAndSet(false, true)) {
            log.info("Cache update triggered. Updating cache in background thread");
            try {
                threadPoolTaskExecutor.execute(() -> {
                    try {
                        putCache();
                    } finally {
                        hashUpdateInProgress.set(false);
                    }
                });
            } catch (Exception e) {
                log.error("Failed to submit task to thread pool", e);
                hashUpdateInProgress.set(false);
            }
        }
    }

    private void generateHashInDatabaseIfNeeded() {
        long count = hashRepository.count();
        log.info("Checking if database needs to be updated, count: {}", count);
        if (count <= databaseProperties.dbSizeToTriggerUpdate() && dbUpdateInProgress.compareAndSet(false, true)) {
            log.info("Database update triggered. Generating hashes in background thread");
            threadPoolTaskExecutor.execute(() -> {
                try {
                    hashGenerator.generateBatch();
                } finally {
                    dbUpdateInProgress.set(false);
                }
            });
        }
    }

    private void cacheWarmUp() {
        if (hashRepository.count() <= cacheProperties.size()) {
            log.info("Database is not yet warmed up. Generating hashes");
            hashGenerator.generateBatch();
        }
        updateCacheIfNeeded();
        log.info("Cache and database was warmed up!");
    }
}
