package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final ExecutorService executorService;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @PostConstruct
    public void init() {
        executorService.submit(this::refillCache);
    }

    @Value("${hash.cache.size}")
    private int cacheSize;

    @Value("${hash.cache.refill_percent}")
    private int refillPercent;

    private final BlockingDeque<String> availableHashes = new LinkedBlockingDeque<>();
    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    @Timed(value = "get_hash_timer", description = "Time taken to get hash",
            histogram = true, percentiles = {0.5, 0.95})
    public Optional<String> getHash() {
        if (shouldRefill()) {
            scheduleRefill();
        }

        try {
            String hash = availableHashes.take();
            return Optional.of(hash);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted while waiting for a hash", e);
            return Optional.empty();
        }
    }

    private boolean shouldRefill() {
        return availableHashes.size() < (cacheSize * refillPercent / 100);
    }

    private void scheduleRefill() {
        if (refillInProgress.compareAndSet(false, true)) {
            executorService.submit(this::refillCache);
        }
    }

    private void refillCache() {
        try {
            hashGenerator.generateBatch();
            List<String> newHashes = hashRepository.getHashBatch(cacheSize);
            availableHashes.addAll(newHashes);
        } finally {
            refillInProgress.set(false);
        }
    }
}
