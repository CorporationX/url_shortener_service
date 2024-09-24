package faang.school.urlshortenerservice.сache;

import faang.school.urlshortenerservice.exception.HashCacheException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final AtomicBoolean isGenerating = new AtomicBoolean(false);
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;

    @Value("${spring.hash_cache.queue_size}")
    private int queueSize;
    @Value("${spring.hash_cache.percentage_to_replenish_queue}")
    private int percentageToReplenishQueue;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    private BlockingQueue<Hash> queueHashes;

    @PostConstruct
    public void initializationQueue() {
        log.info("Initializing Hash Cache");

        queueHashes = new LinkedBlockingDeque<>(queueSize);
        hashGenerator.generateBatch();
        fillQueue();

        log.info("Hash cache initialization complete.");
    }

    @Async("executorService")
    public CompletableFuture<Hash> getHash() {
        if (queueHashes.size() < (queueSize * percentageToReplenishQueue) / 100) {
            if (isGenerating.compareAndSet(false, true)) {
                hashGenerator.generateBatchAsync()
                        .thenRun(this::fillQueue)
                        .thenRun(() -> isGenerating.set(false));
            }
        }

        try {
            return CompletableFuture.completedFuture(queueHashes.poll(3000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            throw new HashCacheException("Queue hashes is empty");
        }
    }

    private void fillQueue() {
        queueHashes.addAll(hashRepository.getHashBatch(batchSize));
    }
}