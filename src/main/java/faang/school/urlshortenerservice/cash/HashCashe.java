package faang.school.urlshortenerservice.cash;

import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCashe {
    private final TaskExecutor queueTaskThreadPool;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isCashing = new AtomicBoolean(false);
    private ArrayBlockingQueue<String> freeCashes;
    @Value("${queue.size}")
    private int queueSize;
    @Value("${queue.percentage-multiplier}")
    private double percentageMultiplier;

    @PostConstruct
    private void init() {
        freeCashes = new ArrayBlockingQueue<>(queueSize);
        hashGenerator.generateBatch();
        freeCashes.addAll(hashRepository.getHashBatch());
        log.info("HashCache initialized, queue is filled");
    }

    public String getHash() {
        if (freeCashes.size() < queueSize * percentageMultiplier
                && isCashing.compareAndSet(false, true)) {
            queueTaskThreadPool.execute(() -> {
                try {
                    freeCashes.addAll(hashRepository.getHashBatch());
                    hashRepository.getHashBatch();
                    log.info("Hash cache will be refilled by {}", Thread.currentThread().getName());
                } finally {
                    isCashing.set(false);
                }
            });
        }
        try {
            return freeCashes.take();
        } catch (InterruptedException e) {
            throw new DataNotFoundException("Something gone wrong while waiting for hash");
        }
    }
}
