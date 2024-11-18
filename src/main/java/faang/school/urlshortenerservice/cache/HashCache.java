package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.model.util.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final TaskExecutor queueTaskThreadPool;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isCaching = new AtomicBoolean(false);
    private ArrayBlockingQueue<String> freeCaches;
    @Value("${hash.queue.size}")
    private int queueSize;
    @Value("${hash.queue.percentage-multiplier}")
    private double percentageMultiplier;

    @PostConstruct
    private void init() {
        freeCaches = new ArrayBlockingQueue<>(queueSize);
        hashGenerator.generateBatch();
        freeCaches.addAll(hashRepository.getHashBatch());
        log.info("HashCache initialized, queue is filled");
    }

    public String getHash() {
        if (freeCaches.size() < queueSize * percentageMultiplier &&
                isCaching.compareAndSet(false, true)) {
            queueTaskThreadPool.execute(() -> {
                try {
                    freeCaches.addAll(hashRepository.getHashBatch());
                    hashGenerator.generateBatch();
                    log.info("hash cache will be refilled by {}", Thread.currentThread().getName());
                } finally {
                    isCaching.set(false);
                }
            });
        }

        try {
            return freeCaches.take();
        } catch (InterruptedException e) {
            throw new DataNotFoundException("Something gone wrong while waiting for hash");
        }
    }
}
