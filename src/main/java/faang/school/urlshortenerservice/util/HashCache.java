package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final LinkedBlockingQueue<String> cache = new LinkedBlockingQueue<>();
    private final Lock lock = new ReentrantLock();

    @Value("${hash.min-hash-cache-size}")
    private int minCacheSize;

    @Value("${hash.cache-percentage-threshold}")
    private double thresholdPercentage;

    public String getHash() {
        double currentPercentage = (double) cache.size() / minCacheSize;

        if (currentPercentage < thresholdPercentage && lock.tryLock()) {
            getNewHashes();
        }

        try {
            return cache.take();
        } catch (InterruptedException e) {
            log.error("Error while retrieving hash from cache", e);
            throw new RuntimeException("Failed to get hash from cache", e);
        }
    }

    @Async("hashCacheThreadPool")
    public void getNewHashes() {
        try {
            log.info("Starting cache refill...");
            List<String> hashes = hashRepository.getAndDeleteHashBatch();
            cache.addAll(hashes);
            hashGenerator.generateBatch();
            log.info("Cache refill complete. Added {} hashes.", hashes.size());
        } finally {
            lock.unlock();
        }
    }
}