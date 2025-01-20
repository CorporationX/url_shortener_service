package faang.school.urlshortenerservice.managers;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

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
    private final ReentrantLock lock = new ReentrantLock();

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

        if (lock.tryLock()) {
            try {
                CompletableFuture<List<Hash>> futureHashes = hashGenerator.getHashBatch();
                futureHashes.whenComplete((hashes, throwable) -> {
                    try {
                        if (throwable == null) {
                            hashes.stream().map(Hash::getHash).forEach(hasheQueue::add);
                            executorService.submit(() -> {
                                List<Hash> newHashes = hashRepository.getHashBatch(capacity);
                                newHashes.stream().map(Hash::getHash).forEach(hasheQueue::add);
                                hashGenerator.generateBatch();
                            });
                        } else {
                            log.error("Failed to get hash batch", throwable);
                        }
                    } finally {
                        lock.unlock();
                    }
                });
            } catch (PersistenceException e) {
                lock.unlock();
                log.error("Exception during hash cache refill", e);
            }
        }
    }
}