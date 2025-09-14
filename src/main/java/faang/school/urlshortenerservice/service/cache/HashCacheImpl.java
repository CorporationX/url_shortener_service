package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.config.properties.hash.HashCacheProperties;
import faang.school.urlshortenerservice.service.generator.HashBatchGenerator;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashCacheImpl implements HashCache {

    private final HashGenerator hashGenerator;
    private final HashBatchGenerator hashBatchGenerator;
    private final HashRepository hashRepository;
    private final HashCacheProperties cacheProperties;
    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);
    private BlockingQueue<String> cache;

    @Qualifier("hashCacheExecutor")
    private final Executor refillExecutor;

    private static final int PERCENT_SCALE = 100;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(cacheProperties.capacity());

        addToCache(hashRepository.getHashBatch(cacheProperties.capacity()));

        if (cache.size() < cacheProperties.capacity()) {
            int missing = cacheProperties.capacity() - cache.size();
            hashBatchGenerator.generateBatch();
            addToCache(hashRepository.getHashBatch(missing));
        }
        log.info("HashCache initialized: size={}/{}", cache.size(), cacheProperties.capacity());
    }

    @Override
    public String getHash() {
        if (isBelowLimit() && refillInProgress.compareAndSet(false, true)) {
            CompletableFuture
                    .runAsync(this::refillFromDb, refillExecutor)
                    .thenCompose(ignored -> generateIfNeeded())
                    .whenComplete((ok, ex) -> {
                        refillInProgress.set(false);
                        if (ex != null) {
                            log.error("Refill error", ex);
                        }
                    });
        }
        return cache.poll();
    }

    private void refillFromDb() {
        if (cache.size() < cacheProperties.capacity()) {
            int free = cacheProperties.capacity() - cache.size();
            List<String> batch = hashRepository.getHashBatch(free);
            addToCache(batch);
            if (!batch.isEmpty()) {
                log.debug("Refilled from DB: {}, size={}/{}", batch.size(), cache.size(), cacheProperties.capacity());
            }
        }
    }

    private CompletableFuture<Void> generateIfNeeded() {
        if (cache.size() >= cacheProperties.capacity()) {
            return CompletableFuture.completedFuture(null);
        }
        return hashGenerator.generateBatchAsync()
                .thenRunAsync(this::refillFromDb, refillExecutor)
                .exceptionally(ex -> {
                    log.error("Async generation failed", ex);
                    return null;
                });
    }

    private boolean isBelowLimit() {
        return (double) cache.size() / cacheProperties.capacity() * PERCENT_SCALE <= cacheProperties.minLimitPercent();
    }

    private void addToCache(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            return;
        }
        int slots = cache.remainingCapacity();
        if (slots <= 0) {
            return;
        }
        hashes.stream()
                .filter(Objects::nonNull)
                .limit(slots)
                .forEach(cache::add);
    }
}
