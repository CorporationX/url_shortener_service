package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.threadpool.ThreadPoolProperties;
import faang.school.urlshortenerservice.generator.HashGenerator;
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
    private final ThreadPoolProperties threadPoolProperties;
    private final Executor hashExecutor;

    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    private final Queue<String> cache = new ConcurrentLinkedQueue<>();

    @PostConstruct
    public void init() {
        refill();
    }

    public String getHash() {
        refill();

        String hash = cache.poll();

        if (hash == null) {
            throw new IllegalStateException("No available hashes in cache");
        }

        return hash;
    }

    private void refill() {
        int currentSize = cache.size();
        var threadPoolPropertiesCache = threadPoolProperties.getCache();
        int threshold = threadPoolPropertiesCache.getMaxSize() * threadPoolPropertiesCache.getRefillThresholdPercent() / 100;
        if (currentSize <= threshold && isRefilling.compareAndSet(false, true)) {
            log.info("Hash cache below threshold ({} of {}). Refilling...", currentSize, threadPoolPropertiesCache.getMaxSize());
            hashExecutor.execute(this::refillCacheAsync);
        }
    }

    private void refillCacheAsync() {
        var threadPoolPropertiesCache = threadPoolProperties.getCache();        try {
            while (cache.size() < threadPoolPropertiesCache.getMaxSize()) {
                var batch = hashRepository.getHashBatch(threadPoolPropertiesCache.getBatchSize());
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