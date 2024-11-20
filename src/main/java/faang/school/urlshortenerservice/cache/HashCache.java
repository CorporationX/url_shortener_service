package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.cache.CacheProperties;
import faang.school.urlshortenerservice.config.executor.ExecutorServiceConfig;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private static final int ONE_HUNDRED = 100;
    private double capacityFactor;

    private final AtomicBoolean filling;
    private final Queue<Hash> hashesCache;

    private final ExecutorServiceConfig executorServiceConfig;
    private final CacheProperties cacheProperties;
    private final HashGenerator hashGenerator;

    @Autowired
    public HashCache(ExecutorServiceConfig executorServiceConfig,
                     HashGenerator hashGenerator,
                     CacheProperties cacheProperties) {
        this.executorServiceConfig = executorServiceConfig;
        this.cacheProperties = cacheProperties;
        this.hashGenerator = hashGenerator;
        this.capacityFactor = (double) ONE_HUNDRED / cacheProperties.getCapacity();
        filling = new AtomicBoolean(false);
        hashesCache = new ArrayBlockingQueue<>(cacheProperties.getCapacity());

        hashesCache.addAll(hashGenerator.getHashesForCache(cacheProperties.getCapacity()));
    }

    @Async("executor")
    public String getHash() {
        if (getFullnessCache() < cacheProperties.getFillPercent()) {
            if (filling.compareAndSet(false, true)) {
                log.info("start filling cache");

                CompletableFuture.supplyAsync(() -> hashGenerator.getHashesBatch(
                        cacheProperties.getCapacity() - hashesCache.size()),
                                executorServiceConfig.executor())
                        .thenAccept(hashesCache::addAll)
                        .thenRun(() -> filling.set(false));

                CompletableFuture.runAsync(hashGenerator::generateBatch, executorServiceConfig.executor());
                log.info("finish filling cache");
            }
        }

        return hashesCache.poll().getHash();
    }

    private double getFullnessCache() {
        return hashesCache.size() * capacityFactor;
    }
}
