package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${cache.max-size}")
    private int capacity;
    @Value("${cache.expand-trigger-percentage}")
    private int percentage;
    @Value("${hash.batchSize}")
    private int hashBatchSize;
    private BlockingQueue<String> cache;
    private Semaphore semaphore;

    private final ExecutorService executorService;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(capacity);
        semaphore = new Semaphore(1);
        hashGenerator.generateBatch();
        cache.addAll(hashRepository.getHashBatch(hashBatchSize));
    }

    public String getHash() {
        int limit =  capacity * (percentage / 100);
        if (cache.size() < limit) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    List<String> hashes = hashRepository.getHashBatch(hashBatchSize);
                    cache.addAll(hashes);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    semaphore.release();
                }
            });
            hashGenerator.generateBatch();
        }
        return cache.poll();
    }
}