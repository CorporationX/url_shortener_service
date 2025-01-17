package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.HashEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final Queue<String> cache = new ConcurrentLinkedQueue<>();
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final ExecutorService executorService;

    @Value("${cache.max-size}")
    private Integer maxSize;

    @Value("${cache.threshold-percentage}")
    private Integer thresholdPercentage;

    private final AtomicBoolean isFetching = new AtomicBoolean(false);
    private volatile CompletableFuture<Void> cacheFillFuture = CompletableFuture.completedFuture(null);

    @PostConstruct
    public void preloadCache() {
        log.info("Starting cache warm-up...");

        triggerAsyncFill().join();

        log.info("Cache warm-up completed, size: {}", cache.size());
    }

    public String getHash() {
        if (cache.size() > maxSize * thresholdPercentage / 100) {
            return getAndRemoveHash(cache);
        }

        triggerAsyncFill();

        return getAndRemoveHash(cache);
    }

    @Scheduled(fixedRateString = "${cache.check-interval}")
    public void checkCacheSize() {
        int currentSize = cache.size();
        int threshold = maxSize * thresholdPercentage / 100;

        log.info("Checking cache size: {} (threshold: {})", currentSize, threshold);

        if (currentSize < threshold) {
            log.warn("Cache size below threshold! Triggering cache refill...");
            triggerAsyncFill();
        }
    }

    private CompletableFuture<Void> triggerAsyncFill() {
        if (isFetching.compareAndSet(false, true)) {
            cacheFillFuture = CompletableFuture.runAsync(this::fillCacheAsync, executorService)
                    .whenComplete((result, error) -> isFetching.set(false));
        }
        return cacheFillFuture;
    }

    private String getAndRemoveHash(Queue<String> cache) {
        String hash = cache.poll();

        HashEntity hashEntity = hashRepository.findByHash(hash);
        if (hashEntity != null) {
            hashEntity.setIsUsed(true);
            hashRepository.save(hashEntity);
        } else {
            log.warn("Hash not found in database: {}", hash);
        }

        log.info("Hash removed from cache: {}", hash);
        log.info("Cache size: {}", cache.size());
        return hash;
    }

    private void fillCacheAsync() {
        try {
            log.info("Adding hashes to cache...");
            List<String> hashes = hashRepository.getAvailableHashes();

            cache.addAll(hashes);

            if (cache.size() < maxSize) {
                List<String> newHashes = hashGenerator.generateBatch(maxSize - cache.size());
                cache.addAll(newHashes);
            }

            log.info("Cache size after filling: {}", cache.size());
        } catch (Exception e) {
            log.error("Error while adding hashes to cache", e);
        }
    }
}

