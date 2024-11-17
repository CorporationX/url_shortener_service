package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    private Queue<String> hashQueue;
    private final ReentrantLock lock = new ReentrantLock();

    @Value("${cache.size}")
    private int cacheSize;

    @Value("${cache.threshold}")
    private double threshold;

    @PostConstruct
    public void init() {
        hashQueue = new ArrayBlockingQueue<>(cacheSize);
        fillCacheSync();
    }

    public String getHash() {
        if (hashQueue.isEmpty()) {
            fillCacheAsync();
        }

        String hash = hashQueue.poll();
        if (hash == null) {
            log.warn("No available hashes in cache");
        }

        if (hashQueue.size() < cacheSize * threshold) {
            fillCacheAsync();
        }

        return hash;
    }

    private void fillCacheSync() {
        lock.lock();
        try {
            performCacheFill();
            log.info("Synchronous cache fill completed. Current size: {}", hashQueue.size());
        } catch (Exception e) {
            log.error("Error during synchronous cache fill", e);
        } finally {
            lock.unlock();
        }
    }

    @Async("fillCacheExecutor")
    public void fillCacheAsync() {
        if (lock.tryLock()) {
            try {
                performCacheFill();
                log.info("Asynchronous cache fill completed. Current size: {}", hashQueue.size());
            } catch (Exception e) {
                log.error("Error during asynchronous cache fill", e);
            } finally {
                lock.unlock();
            }
        } else {
            log.info("Cache fill attempt skipped; already in progress");
        }
    }

    private void performCacheFill() {
        int batchSize = Math.max(cacheSize - hashQueue.size(), 0);
        if (batchSize > 0) {
            hashGenerator.generateBatch();
            List<String> hashes = hashRepository.getHashBatch(batchSize);
            hashQueue.addAll(hashes);
        } else {
            log.info("Cache is full, no need to fill");
        }
    }
}