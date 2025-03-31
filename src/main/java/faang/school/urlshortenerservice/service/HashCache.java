package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService executorService;

    public HashCache(HashRepository hashRepository,
                     HashGenerator hashGenerator,
                     @Qualifier("hashGeneratorExecutor") ExecutorService executorService) {
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
        this.executorService = executorService;
    }

    @Value("${hash.queue.size}")
    private int queueSize;

    @Value("${hash.threshold.limit}")
    private double thresholdLimit;

    @Value("${hash.batch.size}")
    private int batchSize;

    private int threshold;

    private BlockingQueue<String> cacheQueue;
    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        cacheQueue = new ArrayBlockingQueue<>(queueSize);
        threshold = (int) (queueSize * thresholdLimit);

        generateNewHashes().thenRun(() -> fillQueueNewHashes(queueSize));
    }


public String getHash() {
    if (cacheQueue.size() >= threshold) {
        return cacheQueue.poll();
    }

    if (refillInProgress.compareAndSet(false, true)) {
        executorService.submit(() -> {
            try {
                fillQueueNewHashes(batchSize);
                generateNewHashes();
            } catch (Exception e) {
                log.error("Error refilling hash cache: {}", e.getMessage());
            } finally {
                refillInProgress.set(false);
            }
        });
    }

    return cacheQueue.poll();
}

private CompletableFuture<Void> generateNewHashes() {
    return CompletableFuture.runAsync(() -> {
        try {
            hashGenerator.generateBatches();
            log.info("Initialisation hashes queue completed successfully");
        } catch (Exception e) {
            log.error("Error in HashGenerator execution: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }, executorService);
}

private void fillQueueNewHashes(int amount) {
    List<String> newHashes = hashRepository.getHashBatch(amount);
    if (newHashes != null && !newHashes.isEmpty()) {
        cacheQueue.addAll(newHashes);
        log.info("Refilled cache with {} new hashes.", newHashes.size());
    } else {
        log.warn("No new hashes returned from repository.");
    }
}
}
