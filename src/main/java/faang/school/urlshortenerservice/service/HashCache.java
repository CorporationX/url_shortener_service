package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Data
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    @Value("${hash-cache.low-limit-percent:20}")
    private final int lowLimitPercent;
    @Value("${hash-cache.capacity:1000}")
    private final int capacity;
    private final AtomicBoolean areFilling = new AtomicBoolean(false);
    private ArrayBlockingQueue<Hash> queue;

    public Hash getHash() {
        if (queue.size() / (double) capacity * 100 < lowLimitPercent) {
            if (areFilling.compareAndSet(false, true)) {
                fill();
            }
        }
        return queue.poll();
    }

    @Async(value = "hashCacheTaskExecutor")
    private void fill() {
        queue.addAll(hashRepository.getHashBatch(capacity - queue.size()));
        hashGenerator.generateBatch();
        areFilling.set(false);
    }

    @PostConstruct
    private void preFilling() {
        queue = new ArrayBlockingQueue<>(capacity);
        queue.addAll(hashRepository.getHashBatch(capacity));
    }
}
