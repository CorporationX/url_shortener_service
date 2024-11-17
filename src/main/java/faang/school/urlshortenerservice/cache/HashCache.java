package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.custom.DataNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
@Setter
public class HashCache {

    private final TaskExecutor taskThreadPool;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isCaching = new AtomicBoolean(false);

    private ArrayBlockingQueue<String> freeCaches;

    @Value("${hash.cache.size}")
    private int queueSize;

    @Value("${hash.cache.threshold}")
    private static int PERCENTAGE_MULTIPLIER;

    @PostConstruct
    private void initializeCache() {
        freeCaches = new ArrayBlockingQueue<>(queueSize);
        refillCache();
        log.info("HashCache initialized, queue is filled");
    }

    public String getHash() {
        int thresholdSize = (queueSize * PERCENTAGE_MULTIPLIER) / 100;

        if (freeCaches.size() < thresholdSize && isCaching.compareAndSet(false, true)) {
            taskThreadPool.execute(() -> {
                try {
                    refillCache();
                    log.info("Hash cache refilled by {}", Thread.currentThread().getName());
                } finally {
                    isCaching.set(false);
                }
            });
        }

        try {
            return freeCaches.take();
        } catch (InterruptedException e) {
            throw new DataNotFoundException("Error while waiting for a hash: " + e.getMessage());
        }
    }

    private void refillCache() {
        try {
            List<String> newHashes = hashRepository.getHashBatch(queueSize - freeCaches.size());
            freeCaches.addAll(newHashes);

            if (newHashes.size() < queueSize) {
                generateAdditionalHashes();
            }
        } catch (Exception e) {
            log.error("Error while refilling the hash cache: ", e);
        }
    }

    private  void generateAdditionalHashes() {
        log.info("Generating additional hashes...");
        hashGenerator.generateBatch();
    }
}
