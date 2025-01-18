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

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
        triggerAsyncFill();
        log.info("Cache warm-up triggered");
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
            cacheFillFuture = CompletableFuture.supplyAsync(() -> {
                if (cache.isEmpty()) {
                    log.info("Cache is empty, generating new hashes...");
                    List<String> newHashes = hashGenerator.generateBatch(maxSize);

                    List<String> unusedHashes = hashRepository.getAvailableHashes();

                    List<String> hashesToSave = newHashes.stream()
                            .filter(unusedHashes::contains)
                            .collect(Collectors.toList());

                    if (!hashesToSave.isEmpty()) {
                        log.info("Saving new hashes to database...");
                        hashRepository.saveAll(hashesToSave.stream()
                                .map(hash -> HashEntity.builder().hash(hash).isUsed(false).build())
                                .collect(Collectors.toList()));
                    }

                    return hashesToSave;
                }
                return List.of();
            }, executorService).thenAcceptAsync(newHashes -> {
                if (!newHashes.isEmpty()) {
                    cache.addAll((Collection<? extends String>) newHashes);
                }
                fillCacheAsync();
            }, executorService).whenComplete((result, error) -> isFetching.set(false));
        }
        return cacheFillFuture;
    }

    private String getAndRemoveHash(Queue<String> cache) {
        String hash = cache.poll();

        if (hash == null) {
            log.warn("Failed to retrieve hash from cache");
            return null;
        }

        CompletableFuture.runAsync(() -> {
            HashEntity hashEntity = hashRepository.findByHash(hash);
            if (hashEntity != null) {
                hashRepository.markHashAsUsed(hash);
            } else {
                log.warn("Hash not found in the database for hash: {}", hash);
            }
        }, executorService);

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
