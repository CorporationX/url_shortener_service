package faang.school.urlshortenerservice.repository.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Repository
public class HashCache {
    private final long GENERATOR_COEFFICIENT = 5L;
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final ExecutorService executorService;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Value("${hash_cache.capacity}")
    private int capacity;

    @Value("${thread.generate_batch_executor.size}")
    private int generateBatchExecutorSize;


    @Value("${hash_cache.remainder_percent}")
    private int collectionRemainderPercent;

    private Queue<String> cache;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(capacity);
        cache.addAll(hashRepository.getHashBatch(capacity));
    }

    public String getHash() {
        long hashDbSize = hashRepository.count();
        int hashCacheRemainderPercent = cache.size() * 100 / capacity;
        if (hashCacheRemainderPercent <= collectionRemainderPercent) {
            if (!isRunning.getAndSet(true)) {
                executorService.execute(() -> {
                    if (hashDbSize <= capacity * GENERATOR_COEFFICIENT) {
                        IntStream.range(0, generateBatchExecutorSize)
                                .forEach(i -> hashGenerator.generateBatch());
                    }
                    cache.addAll(hashRepository.getHashBatch(capacity - cache.size()));
                    isRunning.set(false);
                });
            }
        }
        return cache.poll();
    }
}
