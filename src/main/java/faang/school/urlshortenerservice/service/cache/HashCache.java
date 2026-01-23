package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;
    private final ThreadPoolTaskExecutor hashCacheExecutor;
    private final HashGenerator hashGenerator;

    private final BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);
    private final ReentrantLock refillLock = new ReentrantLock();

    @Value("${app.hash.batch-size}")
    private int batchSize;

    @Value("${app.hash.cache.refill-threshold-percentage}")
    private int refillThresholdPercentage;

    @Value("${app.hash.cache.max-size}")
    private int maxCacheSize;

    @PostConstruct
    public void init() {
        if (hashRepository.count() == 0) {
            List<String> generatedHashes = hashGenerator.generateBatchSyncAndReturn();
            for (String hash : generatedHashes) {
                if (hashQueue.size() < maxCacheSize) {
                    hashQueue.offer(hash);
                }
            }
        }
        refillCache();
    }

    public String getHash() {
        String hash = hashQueue.poll();
        if (hash != null) {
            checkAndRefillCache();
            return hash;
        }
        return getHashSync();
    }

    public void returnHash(String hash) {
        if (hash != null && hashQueue.size() < maxCacheSize) {
            hashQueue.offer(hash);
        }
    }

    private String getHashSync() {
        try {
            var hashes = hashRepository.getHashBatch(1);
            if (!hashes.isEmpty()) {
                String hash = hashes.get(0);
                scheduleRefillCache();
                return hash;
            }
            generateAndSaveHashes();
            return getHashSync();
        } catch (Exception e) {
            log.error("Failed to get hash synchronously", e);
            return null;
        }
    }

    private void checkAndRefillCache() {
        int currentSize = hashQueue.size();
        int threshold = (maxCacheSize * refillThresholdPercentage) / 100;
        if (currentSize <= threshold) {
            scheduleRefillCache();
        }
    }

    private void scheduleRefillCache() {
        if (isRefilling.compareAndSet(false, true)) {
            hashCacheExecutor.execute(() -> {
                try {
                    refillCache();
                } finally {
                    isRefilling.set(false);
                }
            });
        }
    }

    private void refillCache() {
        try {
            if (refillLock.tryLock()) {
                try {
                    int neededHashes = maxCacheSize - hashQueue.size();
                    if (neededHashes <= 0) {
                        return;
                    }

                    int batchToGet = Math.min(neededHashes, batchSize);
                    var hashes = hashRepository.getHashBatch(batchToGet);
                    if (hashes.isEmpty()) {
                        log.info("No hashes available from DB, generating...");
                        List<String> generatedHashes = hashGenerator.generateBatchSyncAndReturn();
                        for (String hash : generatedHashes) {
                            if (hashQueue.size() < maxCacheSize) {
                                hashQueue.offer(hash);
                            }
                        }
                        return;
                    }
                    for (String hash : hashes) {
                        if (hashQueue.size() < maxCacheSize) {
                            hashQueue.offer(hash);
                        }
                    }
                    log.info("Refilled cache with {} hashes. Current size: {}",
                            hashes.size(), hashQueue.size());

                    if (hashes.size() < batchToGet) {
                        generateAndSaveHashes();
                    }

                } finally {
                    refillLock.unlock();
                }
            }
        } catch (Exception e) {
            log.error("Failed to refill cache", e);
        }
    }

    private void generateAndSaveHashes() {
        try {
            log.info("Start generating and saving new hashes asynchronously in HashGenerator");
            hashGenerator.generateBatch();
           log.info("Successfully generated and saved new hashes asynchronously in HashGenerator ");

        } catch (Exception e) {
            log.error("Failed to generate and save hashes asynchronously in HashGenerator", e);
        }
    }
}
