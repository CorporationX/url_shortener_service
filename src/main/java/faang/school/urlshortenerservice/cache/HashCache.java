package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService executorService;

    private final Queue<String> hashCache = new ConcurrentLinkedQueue<>();
    private final ReentrantLock reentrantLock = new ReentrantLock();

    @Value("${hash.cache.max_size}")
    private int maxSize;

    @Value("${hash.cache.refill_threshold}")
    private double refillThreshold;

    public String getHash() {
        if (hashCache.size() > maxSize * refillThreshold) {
            return hashCache.peek();
        }

        refillCache();
        return hashCache.peek();
    }

    private void refillCache() {
        if (reentrantLock.tryLock()) {
            try {
                if (hashCache.size() < maxSize * refillThreshold) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        List<String> hashes = hashRepository.getHashBatch(maxSize / 2);
                        hashCache.addAll(hashes);

                        hashGenerator.generateBatch();
                    }, executorService);

                    future.join();
                }
            } finally {
                reentrantLock.unlock();
            }
        }
    }
}
