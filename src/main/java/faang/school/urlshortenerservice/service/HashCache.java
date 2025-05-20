package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${url-shortener.cache.capacity}")
    private int cacheCapacity;

    @Value("${url-shortener.cache.fill-percent}")
    private int fillPercent;

    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(cacheCapacity);
        hashes.addAll(hashRepository.getHashBatch(cacheCapacity));
    }

    @Async("cachePoolExecutor")
    public CompletableFuture<String> getHash() {
        if (
                hashes.size() / (cacheCapacity / 100) < fillPercent
                && isFilling.compareAndSet(false, true)
        ) {
            hashRepository.getHashBatch(cacheCapacity - hashes.size());
            hashGenerator.generateBatch();

            isFilling.set(false);
        }
        return CompletableFuture.completedFuture(hashes.poll());
    }
}
