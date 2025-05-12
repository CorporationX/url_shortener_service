package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.ExtractionCacheException;
import faang.school.urlshortenerservice.model.util.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final TaskExecutor queueTaskThreadPool;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isCaching = new AtomicBoolean(false);
    private final ReentrantLock lock = new ReentrantLock();
    private ArrayBlockingQueue<String> freeCaches;

    @Value("${hash.queue.size}")
    @NotNull(message = "Queue size must be specified")
    @Min(value = 1, message = "Queue size must be positive")
    private Integer queueSize;

    @Value("${hash.batch-size}")
    @NotNull(message = "Batch size must be specified")
    @Min(value = 1, message = "Batch size must be positive")
    private Integer batchSize;

    @Value("${hash.queue.percentage-multiplier}")
    @NotNull(message = "Percentage multiplier must be specified")
    @Min(value = 0, message = "Percentage multiplier must be between 0 and 1")
    @Max(value = 1, message = "Percentage multiplier must be between 0 and 1")
    private Double percentageMultiplier;

    @PostConstruct
    private void init() {
        freeCaches = new ArrayBlockingQueue<>(queueSize);
        hashGenerator.generateBatch();
        freeCaches.addAll(hashRepository.getHashBatch(batchSize));
        log.info("HashCache initialized, queue is filled");
    }

    public String getHash() {
        if (freeCaches.size() < queueSize * percentageMultiplier && lock.tryLock()) {
            try {
                if (isCaching.compareAndSet(false, true)) {
                    queueTaskThreadPool.execute(() -> {
                        try {
                            freeCaches.addAll(hashRepository.getHashBatch(batchSize));
                            hashGenerator.generateBatch();
                            log.info("Hash cache refilled by {}", Thread.currentThread().getName());
                        } finally {
                            isCaching.set(false);
                        }
                    });
                }
            } finally {
                lock.unlock();
            }
        }

        try {
            return freeCaches.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExtractionCacheException("Extraction cache has interrupted", e);
        }
    }
}
