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
                if (lock.isLocked()) {
                    log.info("Refilling hash cache");
                    hashGenerator.getHashBatch().join().stream().map(Hash::getHash).forEach(hasheQueue::add);
                    executorService.submit(() -> {
                        List<Hash> hashes = hashRepository.getHashBatch(capacity);
                        hashes.stream().map(Hash::getHash).forEach(hasheQueue::add);
                        hashGenerator.generateBatch();
                    });
                }
            } catch (RuntimeException e) {
                log.error("Failed to acquire lock", e);
            } finally {
                lock.unlock();
            }
        }
    }
}
