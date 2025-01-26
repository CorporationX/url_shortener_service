package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.config.async.AsyncConfig;
import faang.school.urlshortenerservice.model.entity.Hash;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class HashCache {

    private final HashGenerator hashGenerator;
    private final ThreadPoolTaskExecutor cacheLoaderPool;
    private final HashService hashService;

    @Value("${local-cache.hash-cache.capacity}")
    private int cacheCapacity;

    @Value("${local-cache.hash-cache.threshold}")
    private double threshold;

    @Value("${hash-generation.initial-batch-size}")
    private int initialGenerationBatchSize;

    private BlockingQueue<String> cache;
    private final AtomicBoolean filling = new AtomicBoolean(false);

    private static final int PERCENTAGE_MULTIPLIER = 100;

    @Autowired
    public HashCache(HashGenerator hashGenerator,
                     @Qualifier(AsyncConfig.CACHE_LOADER_POOL) ThreadPoolTaskExecutor cacheLoaderPool,
                     HashService hashService) {
        this.hashGenerator = hashGenerator;
        this.cacheLoaderPool = cacheLoaderPool;
        this.hashService = hashService;
    }

    @PostConstruct
    public void init() {
        cache = new LinkedBlockingQueue<>(cacheCapacity);
        hashGenerator.generateHashes(initialGenerationBatchSize);
        loadHashToCache();
    }

    public String getHash() {
        String hash = cache.poll();
        if (isUnderThreshold() && filling.compareAndSet(false, true)) {
            log.info("The number of hashes in cache is below {}%", threshold);

            hashGenerator.generateHashesAsync(getUsedHashSize());
            loadHashToCacheAsync().thenRun(() -> filling.set(false));
        }
        return hash;
    }

    private double calculateFillPercentage() {
        return (cache.size() * 1.0 / cacheCapacity) * PERCENTAGE_MULTIPLIER;
    }

    private boolean isUnderThreshold() {
        return calculateFillPercentage() <= threshold;
    }

    public CompletableFuture<Void> loadHashToCacheAsync() {
        return CompletableFuture.runAsync(this::loadHashToCache,
                cacheLoaderPool);
    }

    public void loadHashToCache() {
        log.info("Hash loading has started");

        hashService.getHashesBatch(getUsedHashSize()).stream()
                .map(Hash::getHash)
                .forEach(cache::offer);
    }

    private int getUsedHashSize() {
        return cacheCapacity - cache.size();
    }
}
