package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Qualifier("hashCacheExecutorService")
    private final ExecutorService executorService;

    @Value("${url-shortener.hash.cache.size}")
    private int cacheSize;

    @Value("${url-shortener.hash.cache.refill-threshold-percent}")
    private int refillThresholdPercent;

    private final ConcurrentLinkedQueue<String> hashQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

      @PostConstruct
    public void init() {
        log.info("Initializing hash cache with size: {}", cacheSize);
        refillCache();
    }

    public String getHash() {
        int currentSize = hashQueue.size();
        log.debug("Current hash cache size: {}", currentSize);

        if (shouldRefillCache(currentSize)) {
            tryRefillCache();
        }

        return hashQueue.poll();
    }

    private boolean shouldRefillCache(int currentSize) {
        int threshold = cacheSize * refillThresholdPercent / 100;
        return currentSize < threshold;
    }

    private void tryRefillCache() {
        if (isRefilling.compareAndSet(false, true)) {
            log.info("Starting async cache refill");
            executorService.submit(this::refillCache);
        }
    }

    private void refillCache() {
        try {
            log.info("Refilling hash cache");

            List<String> hashes = hashRepository.getHashBatch();
            log.debug("Retrieved {} hashes from repository", hashes.size());

            hashQueue.addAll(hashes);
            log.info("Added {} hashes to cache, new size: {}", hashes.size(), hashQueue.size());

            hashGenerator.generateBatch();
        } catch (Exception e) {
            log.error("Error while refilling hash cache", e);
        } finally {
            isRefilling.set(false);
            log.debug("Cache refill process completed");
        }
    }
}