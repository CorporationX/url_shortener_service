package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.model.util.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Queue;
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
    private Queue<String> freeCaches;
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
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                queueTaskThreadPool.execute(() -> {
                    freeCaches.addAll(hashRepository.getHashBatch());
                    hashGenerator.generateBatch();
                    log.info("hash cache will be refilled by {}", Thread.currentThread().getName());
                });
            } finally {
                isCaching.set(false);
            }
        }

        return freeCaches.poll();
    }
}
