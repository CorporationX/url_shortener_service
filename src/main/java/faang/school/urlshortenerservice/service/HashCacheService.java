package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.component.HashBatchFetcher;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashCacheService {

    private final HashBatchFetcher hashBatchFetcher;
    private final HashRepository hashRepository;
    private final ThreadPoolTaskExecutor executor;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Value("${hash-cache-setting.queue-size}")
    private int size;

    @Value("${hash-cache-setting.percentage-to-generate-new-hashes}")
    private int percentageToGenerateNewHashes;

    @Value("${hash-cache-setting.fetch-batch-size}")
    private int batchSize;

    private BlockingQueue<String> hashesCacheQueue;

    @PostConstruct
    public void init() {
        hashesCacheQueue = new LinkedBlockingQueue<>(size);
        if (hashRepository.existsHashesAtLeast(size)) {
            while (hashesCacheQueue.size() < size) {
                refillQueue();
            }
        }
    }

    public String getHash() {
        double fillPercentage = ((double) hashesCacheQueue.size() / size) * 100;
        if (fillPercentage < percentageToGenerateNewHashes) {
            if (isRunning.compareAndSet(false, true)) {
                CompletableFuture.runAsync(this::refillQueue, executor)
                        .thenRun(() -> isRunning.set(false));
            }
        }
        return hashesCacheQueue.poll();
    }

    private void refillQueue() {
        int remainingCapacity = size - hashesCacheQueue.size();
        int totalToLoad = Math.min(remainingCapacity, batchSize);

        if (totalToLoad <= 0) {
            return;
        }
        List<String> hashes = hashBatchFetcher.fetchHashes(totalToLoad);
        hashesCacheQueue.addAll(hashes);
        log.info("Refilled {} hashes from DB", hashes.size());
    }
}
