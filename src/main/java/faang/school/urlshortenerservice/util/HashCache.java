package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
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

    private final BlockingQueue<String> cache = new ArrayBlockingQueue<>(capacity);
    private final Semaphore semaphore = new Semaphore(1);
    private final ExecutorService executorService;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

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