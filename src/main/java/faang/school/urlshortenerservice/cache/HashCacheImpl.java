package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.properties.CacheProperties;
import faang.school.urlshortenerservice.generator.HashGenerator;
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
    private final CacheProperties properties;
    private final HashGenerator generator;
    private final HashRepository repository;
    private final ThreadPoolTaskExecutor executor;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);
    private final Queue<String> hashes;

    public HashCacheImpl(CacheProperties properties,
                         HashGenerator generator,
                         HashRepository repository,
                         @Qualifier("hashCacheExecutor") ThreadPoolTaskExecutor executor) {
        this.properties = properties;
        this.generator = generator;
        this.repository = repository;
        this.executor = executor;
        this.hashes = new LinkedBlockingQueue<>(properties.capacity());
    }

    @PostConstruct
    public void initCache() {
        log.info("Initializing hash cache with capacity: {}", properties.capacity());
        long availableHashesInDb = repository.count();
        if (availableHashesInDb == 0) {
            log.warn("No hashes found in database. Generating initial batch...");
            generator.generateBatch();
        }

        List<String> hashBatch = repository.getHashBatch(properties.capacity());
        hashes.addAll(hashBatch);
        log.info("Hash cache initialized with {} hashes", hashes.size());
    }

    @Override
    public String getHash() {
        String hash = hashes.poll();
        int currentHashSize = hashes.size();
        int thresholdHashSize = properties.capacity() * properties.threshold() / 100;

        if (currentHashSize < thresholdHashSize) {
            log.debug("Cache size ({}) below threshold ({}). Triggering refill...",
                    currentHashSize, thresholdHashSize);
            refillAsync();
        }

        return hash;
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
        }
    }

    private void refillCache() {
        List<String> hashBatch = repository.getHashBatch(properties.batchSize());
        hashes.addAll(hashBatch);
        generator.generateBatchAsync();
    }
}
