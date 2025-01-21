package faang.school.urlshortenerservice.hesh;

import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
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
    @Value("${hash.batch-size}")
    private int batchSize;
    @Autowired
    public HashCache(@Qualifier("hashGeneratorExecutor") TaskExecutor queueTaskThreadPool,
                     HashRepository hashRepository,
                     HashGenerator hashGenerator) {
        this.queueTaskThreadPool = queueTaskThreadPool;
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
    }
    @PostConstruct
    private void init() {
        freeCaches = new ArrayBlockingQueue<>(queueSize);
        hashGenerator.generateBatch();
        freeCaches.addAll(hashRepository.getHashBatch(batchSize));
        log.info("HashCache initialized, queue is filled");
    }

    public String getHash() {
        if (freeCaches.size() < queueSize * percentageMultiplier &&
                isCaching.compareAndSet(false, true)) {
            queueTaskThreadPool.execute(() -> {
                try {
                    freeCaches.addAll(hashRepository.getHashBatch(batchSize));
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