package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final ThreadPoolTaskExecutor poolTaskExecutor;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @Value("${hash-service.cache.cache-size}")
    private int cacheSize;

    @Value("${hash-service.cache.percentage-filling}")
    private int refillThreshold;

    @Value("${hash-service.count-get-hash}")
    public int countHash;
    private ArrayBlockingQueue<Hash> hashQueue;

    @PostConstruct
    public void init() {
        hashQueue = new ArrayBlockingQueue<>(cacheSize);
        refillCache();
    }

    public Hash getHash() {
        if (hashQueue.size() <= cacheSize * refillThreshold / 100) {
            refillCache();
        }
        return hashQueue.poll();
    }

    private void refillCache() {
        hashGenerator.generateBatch();
        if (isRefilling.compareAndSet(false, true)) {
            poolTaskExecutor.execute(() -> {
                hashQueue.addAll(hashRepository.getHashBatches(countHash));
                isRefilling.set(false);
            });
        }
    }
}
