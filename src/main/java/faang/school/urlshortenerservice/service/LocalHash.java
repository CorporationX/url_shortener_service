package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalHash {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    private final Deque<String> hashDeque = new ConcurrentLinkedDeque<>();
    private final Semaphore refillSemaphore = new Semaphore(1);
    private final Semaphore generateNewHashesSemaphore = new Semaphore(1);
    private final ExecutorService executorService;

    @Value("${cache.max-size:10000}")
    private int maxCacheSize;

    @Value("${cache.min-threshold-percent:20}")
    private int minThresholdPercent;

    @PostConstruct
    public void initializeCache() {
        try {
            refillCacheSynchronously();
        } catch (Exception e) {
            log.warn("Failed to initialize hash cache", e);
        }
    }

    public String getHash() {
        String hash = hashDeque.pollFirst();

        if (hash == null) {
            log.warn("Hash cache is empty, refilling synchronously");
            refillCacheSynchronously();
            hash = hashDeque.pollFirst();

            if (hash == null) {
                throw new RuntimeException("Unable to generate hash - database might be unavailable");
            }
        }

        checkAndRefillAsync();

        return hash;
    }

    private void checkAndRefillAsync() {
        int currentSize = hashDeque.size();
        int threshold = (maxCacheSize * minThresholdPercent) / 100;

        if (currentSize < threshold) {
            log.debug("Hash cache level: {} (threshold: {}), refilling asynchronously",
                    currentSize, threshold);

            CompletableFuture.runAsync(this::refillCacheSynchronously, executorService)
                    .exceptionally(ex -> {
                        log.error("Error during async cache refill", ex);
                        return null;
                    });
        }
    }

    private void refillCacheSynchronously() {
        if (!refillSemaphore.tryAcquire()) {
            log.debug("Refill already in progress, skipping");
            return;
        }

        long startTime = System.currentTimeMillis();
        try {
            int needed = maxCacheSize - hashDeque.size();
            if (needed <= 0) {
                return;
            }

            List<Hash> unusedHashes = addToHashDeque(needed);

            if (unusedHashes.size() < needed) {
                needed -= unusedHashes.size();

                generateNewHashesAsync().join();

                unusedHashes = addToHashDeque(needed);
            }

            generateNewHashesAsync();

            long duration = System.currentTimeMillis() - startTime;
            log.warn("Refilled hash cache with {} hashes in {} ms",
                    String.format("%,d", unusedHashes.size()),
                    String.format("%,d", duration));
        } finally {
            refillSemaphore.release();
        }
    }

    private List<Hash> addToHashDeque(int needed) {
        List<Hash> unusedHashes = hashRepository.findAndDelete(needed);

        for (Hash hash : unusedHashes) {
            hashDeque.addLast(hash.getHashValue());
        }
        return unusedHashes;
    }

    private CompletableFuture<Void> generateNewHashesAsync() {
        return CompletableFuture.runAsync(() -> {
            if (!generateNewHashesSemaphore.tryAcquire()) {
                log.debug("Generate new hashes already in progress, skipping");
                return;
            }

            try {
                long unusedCount = hashRepository.countUnusedHashes();
                long threshold = maxCacheSize * 10L * minThresholdPercent / 100;

                if (unusedCount <= threshold) {
                    log.warn("Start generating {} new hashes asynchronously (unused: {})",
                            String.format("%,d", maxCacheSize * 10),
                            String.format("%,d", unusedCount));

                    long startTime = System.currentTimeMillis();
                    hashGenerator.generateAndSaveHashes(maxCacheSize * 10);

                    long duration = System.currentTimeMillis() - startTime;
                    log.warn("Generated {} new hashes in {} ms",
                            String.format("%,d", maxCacheSize * 10),
                            String.format("%,d", duration));
                }
            } catch (Exception e) {
                log.error("Error generating new hashes", e);
            } finally {
                generateNewHashesSemaphore.release();
            }
        }, executorService);
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS)) {
                log.warn("Executor service did not terminate within 30 seconds, forcing shutdown");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for executor service shutdown", e);
            executorService.shutdownNow();
        }
    }
}

