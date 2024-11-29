package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ConcurrentLinkedQueue<String> hashQueue;
    private final int cacheSize;
    private final double threshold;
    private final ReentrantLock lock = new ReentrantLock();

    public HashCache(HashRepository hashRepository, HashGenerator hashGenerator,
                     @Value("${cache.size}") int cacheSize,
                     @Value("${cache.threshold}") double threshold) {
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
        this.cacheSize = cacheSize;
        this.threshold = threshold;
        this.hashQueue = new ConcurrentLinkedQueue<>();

        fillCacheAsync();
    }

    public String getHash() {
        String hash = hashQueue.poll();
        if (hashQueue.size() < cacheSize * threshold) {
            fillCacheAsync();
        }
        return hash;
    }

    @Async("fillCacheExecutor")
    public void fillCacheAsync() {
        if (lock.tryLock()) {
            try {
                performCacheFill();
                log.debug("Asynchronous cache fill completed. Current size: {}", hashQueue.size());
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
            List<String> hashes = hashRepository.getHashBatch(batchSize);
            hashGenerator.generateBatch();
            hashQueue.addAll(hashes);
            log.debug("Added {} hashes to cache. Current cache size: {}", hashes.size(), hashQueue.size());
        } else {
            log.info("Cache is full, no need to fill");
        }
    }
}