package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Service
public class HashCacheImpl implements HashCache {
    @Value("${hash.cache.capacity}")
    private int cacheCapacity;

    @Value("${hash.cache.min-limit-percent}")
    private int minLimitPercent;
    private final Executor executor;
    private final HashGenerator generator;
    private final HashRepository hashRepository;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private Queue<String> caches;

    @PostConstruct
    public void init() {
        caches = new ArrayBlockingQueue<>(cacheCapacity);
        List<String> hashes = generator.generateBatch();
        caches.addAll(hashes);
    }

    @Override
    @Transactional
    public String getHash() {
        if (isBelowLimit()) {
            if (!isFilling.compareAndExchange(false, true)) {
                CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                    int fetchSize = cacheCapacity - caches.size();
                    List<String> hashBatch = hashRepository.getHashBatch(fetchSize);
                    caches.addAll(hashBatch);
                    if (hashBatch.size() < fetchSize) {
                        generator.generateBatch();
                    }
                }, executor).thenRun(() -> isFilling.set(false));
            }
        }
        return caches.poll();
    }

    private boolean isBelowLimit() {
        return (double) caches.size() / cacheCapacity * 100 <= minLimitPercent;
    }
}