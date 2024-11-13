package faang.school.urlshortenerservice.repository.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Repository
public class HashCache {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final ExecutorService executorService;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    @Value("${hash_cache.capacity}")
    private int capacity;

    private Queue<String> cache;

    @Value("${hash_cache.remainder_percent}")
    private int collectionRemainderPercent;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(capacity);

        cache.addAll(hashRepository.getHashBatch(capacity));
//        long hashDbSize = hashRepository.count();
//        if (hashDbSize <= capacity) {
//            hashGenerator.generateBatch();
//        }
    }

    public String getHash() throws ExecutionException, InterruptedException {
        long hashDbSize = hashRepository.count();
        System.out.println("HasdCacheSize: " + cache.size());


        int hashCacheRemainderPercent = cache.size() * 100 / capacity;
        System.out.println("2222222HashCacheRemainderPercent: " + hashCacheRemainderPercent);
        System.out.println("333333CollecitonRemainderPercent: " + collectionRemainderPercent);
        if (hashCacheRemainderPercent <= collectionRemainderPercent) {
            System.out.println("isRunning before start batch and generate hash " + isRunning.get());
            if (!isRunning.getAndSet(true)) {
                System.out.println("isRunning " + isRunning.get());
                System.out.println("Starting batch add to HashCache");
                executorService.execute(() -> {
                    // TODO adsf
                    if (hashDbSize <= capacity*2L) {
                            hashGenerator.generateBatch();
                            System.out.println("Start generator");
                    }
                    cache.addAll(hashRepository.getHashBatch(capacity - cache.size()));
                    isRunning.set(false);
                });
            }

        }
        //TODO check multithreading
        return cache.poll();
    }
// TODO afdfg
}
