package faang.school.urlshortenerservice.hash;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private final Executor hashProducerExecutor;
    private final Executor hashConsumerExecutor;

    @Value("${hash.storage.capacity}")
    private int capacity;
    @Value("${hash.db.refresh-threshold-count}")
    private int dbThreshold;
    @Value("${hash.storage.refresh-threshold-percent}")
    private double cacheThreshold;

    private Queue<String> localCache;
    private AtomicBoolean isCacheRefreshing;

    @PostConstruct
    public void init() {
        localCache = new ArrayBlockingQueue<>(capacity);
        isCacheRefreshing = new AtomicBoolean(false);

        hashGenerator.generateBatch();
        localCache.addAll(hashGenerator.getHashBatch(capacity));
    }

    public String getHash() {
        log.info("Beginning to retrieve hash from cache.");
        if ((localCache.size() <= (capacity * cacheThreshold))
                && (isCacheRefreshing.compareAndSet(false, true))) {
            log.debug("Updating the cache.");
            CompletableFuture
                    .supplyAsync(() -> hashGenerator
                            .getHashBatch(capacity - localCache.size()), hashConsumerExecutor)
                    .thenAccept(localCache::addAll)
                    .thenRun(() -> isCacheRefreshing.set(false))
                    .thenRunAsync(this::generateHashesIfThresholdReached, hashProducerExecutor);
        }
        log.info("Successfully retrieving a hash from a cache.");
        return localCache.poll();
    }

    private void generateHashesIfThresholdReached() {
        if (hashGenerator.isHashCountBelowThreshold(dbThreshold) && hashGenerator.tryLock()) {
            log.debug("Adding new hashes to the database.");
            try {
                hashGenerator.generateBatch();
            } finally {
                hashGenerator.unlock();
            }
        }
    }
}