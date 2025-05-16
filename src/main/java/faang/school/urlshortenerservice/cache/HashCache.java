package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final HashProperties hashProperties;
    private final Executor hashCacheExecutor;

    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    private final Queue<String> cache = new ConcurrentLinkedQueue<>();

    @PostConstruct
    public void init() {
        log.info("Initializing HashCache...");
        if (cache.isEmpty()) {
            maybeRefill();
        }
    }

    public String getHash() {
        maybeRefill();

        String hash = cache.poll();

        if (hash == null) {
            log.error("Hash cache is empty! Possibly waiting for async refill...");
            throw new IllegalStateException("No available hashes in cache");
        }

        return hash;
    }

    private void maybeRefill() {
        int currentSize = cache.size();
        var cacheProps = hashProperties.getCache();
        int threshold = cacheProps.getMaxSize() * cacheProps.getRefillThresholdPercent() / 100;

        if (currentSize <= threshold && isRefilling.compareAndSet(false, true)) {
            log.info("Hash cache below threshold ({} of {}). Refilling...", currentSize, hashProperties.getCache().getMaxSize());
            hashCacheExecutor.execute(this::refillCacheAsync);
        }
    }

    private void refillCacheAsync() {
        try {
            while (cache.size() < hashProperties.getCache().getMaxSize()) {
                var batch = hashRepository.getHashBatch(hashProperties.getCache().getBatchSize());
                if (batch.isEmpty()) {
                    log.warn("No hashes available in DB. Triggering generator...");
                    hashGenerator.generateBatch();
                    break;
                }
                cache.addAll(batch);
                log.info("Refilled {} hashes into cache", batch.size());
            }
        } catch (Exception e) {
            log.error("Error while refilling hash cache", e);
        } finally {
            isRefilling.set(false);
        }
    }
}
