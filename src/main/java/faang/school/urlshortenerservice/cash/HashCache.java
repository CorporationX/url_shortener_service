package faang.school.urlshortenerservice.cash;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final TaskExecutor queueTaskThreadPool;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isCaching = new AtomicBoolean(false);
    private ArrayBlockingQueue<String> freeCaches;
    @Value("${queue.size}")
    private int queueSize;
    @Value("${queue.percentage-multiplier}")
    private double percentageMultiplier;
    @Value("${hash.batch-size}")
    private int batchSize;

    @PostConstruct
    private void init() {
        freeCaches = new ArrayBlockingQueue<>(queueSize);
        hashGenerator.generateBatch()
                .thenRun(() -> {
                    fillQueueSafely(hashRepository.getHashBatch(batchSize));
                    log.info("HashCache initialized, queue is filled");
                })
                .exceptionally(e -> {
                    log.error("Error initializing HashCache: ", e);
                    throw new IllegalStateException("Failed to initialize HashCache", e);
                });
    }

    public String getHash() {
        if (freeCaches.size() < queueSize * percentageMultiplier
                && isCaching.compareAndSet(false, true)) {
            queueTaskThreadPool.execute(() -> {
                try {
                    fillQueueSafely(hashRepository.getHashBatch(batchSize));
                    hashGenerator.generateBatch();
                    log.info("Hash cache will be refilled by {}", Thread.currentThread().getName());
                } catch (Exception e) {
                    log.error("Error occurred while refilling the hash cache: {}", e.getMessage(), e);
                } finally {
                    isCaching.set(false);
                }
            });
        }
        String hash = freeCaches.poll();
        if (hash == null) {
            log.warn("Hash queue is empty, returning null");
        }
        return hash;
    }

    private void fillQueueSafely(List<String> hashes) {
        for (String hash : hashes) {
            boolean added = freeCaches.offer(hash);
            if (!added) {
                break;
            }
        }
    }
}
