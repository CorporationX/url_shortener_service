package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.component.HashBatchFetcher;
import faang.school.urlshortenerservice.properties.HashCacheProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final HashCacheProperties properties;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private int size;

    private BlockingQueue<String> hashesCacheQueue;

    @PostConstruct
    public void init() {
        size = properties.queueSize();
        hashesCacheQueue = new LinkedBlockingQueue<>(size);
        if (hashRepository.existsHashesAtLeast(size)) {
            refillQueueSync();
        }
    }

    public String getHash() {
        double fillPercentage = ((double) hashesCacheQueue.size() / size) * 100;
        if (fillPercentage < properties.percentageToGenerateNewHashes()) {
            if (isRunning.compareAndSet(false, true)) {
                CompletableFuture.runAsync(this::refillQueue, executor)
                        .thenRun(() -> isRunning.set(false));
            }
        }
        return hashesCacheQueue.poll();
    }

    public void refillQueueSync() {
        log.info("Check needed refilling cache hash queue");
        while (hashesCacheQueue.size() < size) {
            refillQueue();
        }
    }

    private void refillQueue() {
        int remainingCapacity = size - hashesCacheQueue.size();
        int totalToLoad = Math.min(remainingCapacity, properties.batchSize());

        if (totalToLoad <= 0) {
            return;
        }
        List<String> hashes = hashBatchFetcher.fetchHashes(totalToLoad);
        hashesCacheQueue.addAll(hashes);
        log.info("Refilled {} hashes to hash cache queue", hashes.size());
    }
}
