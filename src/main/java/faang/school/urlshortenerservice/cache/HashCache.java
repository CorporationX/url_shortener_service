package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCache {

    @Value("${params.cache.percentage}")
    private double minPercentage;
    @Value("${params.batch-size}")
    private int batchSize;

    private final ArrayBlockingQueue<String> queue;
    private final ExecutorService executorService;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @Transactional
    public String getHash() {
        if (!(hashPercentage() > minPercentage)) {
            checkAndFillCache();
        }
        return waitForHash();
    }

    private void checkAndFillCache() {
        if (isFilling.compareAndSet(false, true)) {
            executorService.execute(() -> {
                log.info("Starting adding {} hashes to the queue", batchSize);
                List<String> hashBatch = hashRepository.getHashBatch(batchSize);
                hashGenerator.generateHash();
                queue.addAll(hashBatch);
                log.info("{} hashes added to the queue", batchSize);
                isFilling.set(false);
            });
        }
    }

    private String waitForHash() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private double hashPercentage() {
        int capacity = queue.remainingCapacity() + queue.size();
        return (double) queue.size() / capacity;
    }
}
