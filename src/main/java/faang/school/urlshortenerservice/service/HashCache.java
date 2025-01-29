package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCache {
    private final int batchSize;
    private final int queueSize;
    private final double capacityFactorForUpdate;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ArrayBlockingQueue<String> hashQueue;
    private final AtomicBoolean isUpdating;
    private final ExecutorService executorService;

    public HashCache(
            HashGenerator hashGenerator,
            HashRepository hashRepository,
            @Value("${hash-caching.batch-size}") int batchSize,
            @Value("${hash-caching.queue-size}") int queueSize,
            @Value("${hash-caching.capacity-factor-for-update}") double capacityFactorForUpdate
    ) {
        this.hashGenerator = hashGenerator;
        this.hashRepository = hashRepository;
        this.batchSize = batchSize;
        this.queueSize = queueSize;
        this.capacityFactorForUpdate = capacityFactorForUpdate;
        this.hashQueue = new ArrayBlockingQueue<>(queueSize);
        this.executorService = Executors.newFixedThreadPool(1);
        this.isUpdating = new AtomicBoolean(true);

        updateHashes();
    }

    public String getHash() {
        if (needUpdateQueue() && isUpdating.compareAndSet(false, true)) {
            executorService.submit(this::updateHashes);
        }
        try {
            return hashQueue.take();
        } catch (InterruptedException e) {
            log.error("Thread was interrupted", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted", e);
        }
    }

    private boolean needUpdateQueue() {
        return (double) hashQueue.size() / queueSize < capacityFactorForUpdate;
    }

    private void updateHashes() {
        try {
            List<String> hashes = hashRepository.getHashBatch(batchSize);
            hashGenerator.generateBatch();

            for (String hash : hashes) {
                hashQueue.put(hash);
            }

        } catch (InterruptedException e) {
            log.error("Thread was interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            isUpdating.set(false);
        }
    }

}
