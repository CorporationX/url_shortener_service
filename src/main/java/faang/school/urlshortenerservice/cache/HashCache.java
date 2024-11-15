package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${params.cache.capacity}")
    private int capacity;
    @Value("${params.cache.percentage}")
    private double minPercentage;
    @Value("${params.batch-size}")
    private int batchSize;
    private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(capacity);
    private final ExecutorService executorService;
    private final HashRepository hashRepository;

    public String getHash() {
        double hashPercentage = (double) queue.size() / capacity;
        if (hashPercentage > minPercentage) {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            executorService.submit(() -> {
                List<String> hashBatch = hashRepository.getHashBatch(batchSize);
                queue.addAll(hashBatch);

            });
            try {
                return queue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
