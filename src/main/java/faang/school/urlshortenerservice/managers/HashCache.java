package faang.school.urlshortenerservice.managers;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.refill_threshold}")
    private double refillThreshold;

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService executorService;
    private Queue<String> hasheQueue;
    protected AtomicBoolean isRefilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        hasheQueue = new ArrayBlockingQueue<>(capacity);
        hashGenerator.getHashBatchSync().stream().map(Hash::getHash).forEach(hasheQueue::add);
    }

    public String getHash() {
        if (hasheQueue.size() < capacity * refillThreshold) {
            log.info("Refilling hash cache");
            refillHashCache();
        }
        return hasheQueue.poll();
    }

    protected void refillHashCache() {
        if (isRefilling.compareAndSet(false, true)) {
            CompletableFuture<List<Hash>> futureHashes = hashGenerator.getHashBatch();
            futureHashes.thenAccept(hashes -> {
                try {
                    hashes.stream().map(Hash::getHash).forEach(hasheQueue::add);
                    executorService.submit(() -> {
                        List<Hash> newHashes = hashRepository.getHashBatch(capacity);
                        newHashes.stream().map(Hash::getHash).forEach(hasheQueue::add);
                        hashGenerator.generateBatch();
                    });
                } catch (Exception e) {
                    log.error("Exception during hash cache refill", e);
                } finally {
                    isRefilling.set(false);
                }
            }).exceptionally(throwable -> {
                log.error("Failed to get hash batch", throwable);
                isRefilling.set(false);
                return null;
            });
        }
    }
}