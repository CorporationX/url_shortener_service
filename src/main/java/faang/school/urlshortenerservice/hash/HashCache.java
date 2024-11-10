package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${executor.queueCapacity}")
    private int cacheSize;

    private final ExecutorService executorService;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private BlockingQueue<String> hashQueue;

    double howTo20Percent;

    @PostConstruct
    private void init() {
        this.hashQueue = new LinkedBlockingQueue<>(cacheSize);
        this.howTo20Percent = cacheSize * 0.2;
    }

    public String getHash() {
        if (hashQueue.size() > howTo20Percent) {
            return hashQueue.poll();
        } else {
            if (isRefilling.compareAndSet(false, true)) {
                synchronized (hashQueue) {
                    executorService.execute(() -> {
                        int batchSize = cacheSize - hashQueue.size();
                        List<String> hashes = hashRepository.getHashBatch(batchSize);
                        hashes.forEach(System.out::println);
                        log.info("Before adding to queue: " + hashQueue);
                        hashes.forEach(hashQueue::offer);
                        log.info("After adding to queue: " + hashQueue);
                        hashGenerator.generateBatch();
                        isRefilling.set(false);
                    });
                }
            }
            while (hashQueue.isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return hashQueue.poll();
        }
    }
}
