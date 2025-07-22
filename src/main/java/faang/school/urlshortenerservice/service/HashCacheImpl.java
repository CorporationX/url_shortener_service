package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.CacheProperties;
import faang.school.urlshortenerservice.exception.HashCacheException;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class HashCacheImpl implements HashCache {
    private final CacheProperties props;
    private final HashGenerator generator;
    private final HashRepository repository;
    private final ThreadPoolTaskExecutor executor;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);
    private final Queue<String> hashes;

    public HashCacheImpl(CacheProperties props,
                         HashGenerator generator,
                         HashRepository repository,
                         @Qualifier("hashCacheExecutor") ThreadPoolTaskExecutor executor) {
        this.props = props;
        this.generator = generator;
        this.repository = repository;
        this.executor = executor;
        this.hashes = new LinkedBlockingQueue<>(props.capacity());
    }

    @PostConstruct
    public void initCache() {
        log.info("Initializing hash cache with capacity: {}", props.capacity());
        long availableInDb = repository.count();
        log.info("Found {} hashes available in database", availableInDb);
        if (availableInDb == 0) {
            log.warn("No hashes found in database. Generating initial batch...");
            generator.generateBatch();
        }

        loadFromDatabase();
        log.info("Hash cache initialized with {} hashes", hashes.size());
    }

    @Override
    public String getHash() {
        String hash = hashes.poll();
        int currentSize = hashes.size();
        int thresholdSize = props.capacity() * props.threshold() / 100;

        if (currentSize < thresholdSize) {
            log.debug("Cache size ({}) below threshold ({}). Triggering refill...",
                    currentSize, thresholdSize);
            refillAsync();
        }

        return hash;
    }

    private void loadFromDatabase() {
        try {
            int toLoad = Math.min(props.capacity(), (int) repository.count());
            if (toLoad > 0) {
                List<String> hashBatch = repository.getHashBatch(toLoad);
                int loaded = 0;
                for (String hash : hashBatch) {
                    if (hashes.offer(hash)) {
                        loaded++;
                    } else {
                        break;
                    }
                }
                log.info("Loaded {} hashes from database into cache", loaded);
            } else {
                log.warn("No hashes available to load from database");
            }
        } catch (Exception e) {
            log.error("Error loading hashes from database", e);
            throw new HashCacheException("Failed to load hashes from database", e);
        }
    }

    private void refillAsync() {
        if (isRefilling.compareAndSet(false, true)) {
            log.debug("Starting async refill process");

            executor.execute(() -> {
                try {
                    refillCache();
                } finally {
                    isRefilling.set(false);
                    log.debug("Async refill process completed");
                }
            });
        } else {
            log.debug("Refill already in progress, skipping");
        }
    }

    private void refillCache() {
        try {
            int neededForCache = props.capacity() - hashes.size();
            if (neededForCache > 0) {
                List<String> hashBatch = repository.getHashBatch(
                        Math.min(neededForCache, props.batchSize())
                );

                int added = 0;
                for (String hash : hashBatch) {
                    if (hashes.offer(hash)) {
                        added++;
                    }
                }
                log.debug("Added {} hashes to cache during refill", added);
            }
            log.debug("Triggering background hash generation");
            generator.generateBatchAsync();

        } catch (Exception e) {
            log.error("Error during cache refill", e);
        }
    }
}
