package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepositoryJdbc;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashRepositoryJdbc hashRepository;
    private final ExecutorService hashExecutorService;
    private final HashGenerator hashGenerator;

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.refill-threshold}")
    private int refillThresholdPercentage;

    private final Queue<String> hashCache = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        refillCache(capacity);
        if (hashCache.size() < capacity) {
            hashGenerator.syncGenerateBatch();
            refillCache(capacity - hashCache.size());
        }
    }

    public String getHash() {
        int refillThreshold = (int) (capacity * (refillThresholdPercentage / 100.0));

        if (hashCache.size() > refillThreshold) {
            return hashCache.poll();
        }

        asyncRefillCache();
        hashGenerator.generateBatch();

        return hashCache.poll();
    }

    private void asyncRefillCache() {
        if (isRefilling.compareAndSet(false, true)) {
            hashExecutorService.submit(() -> {
                try {
                    refillCache(capacity - hashCache.size());
                    hashGenerator.generateBatch();
                } catch (Exception e) {
                    log.error("Error during cache refill", e);
                } finally {
                    isRefilling.set(false);
                }
            });
        }
    }

    private void refillCache(int amount) {
        log.info("Refilling hash cache...");

        List<String> newHashes = hashRepository.getHashBatch(amount);
        hashCache.addAll(newHashes);

        log.info("Hash cache refilled with {} items.", newHashes.size());
    }
}
