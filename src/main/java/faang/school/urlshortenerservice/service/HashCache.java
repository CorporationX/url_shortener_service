package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Log4j2
public class HashCache {

    @Value("${hash.cache.threshold.size}")
    private int cacheSize;
    @Value("${hash.cache.threshold.percentage}")
    private double thresholdPercentage;

    private LinkedBlockingQueue<String> hashQueue;

    private final ExecutorService executorService;
    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);

    private final HashRepository hashRepository;

    private final HashGenerator hashGenerator;

    @PostConstruct
    public void initializeQueue() {
        log.info("Initializing hash queue with cache size: {}", cacheSize);
        this.hashQueue = new LinkedBlockingQueue<>(cacheSize);
    }

    public String getHash() {
        double threshold = cacheSize * (thresholdPercentage / 100.0);
        if (hashQueue.size() > threshold) {
            return hashQueue.poll();
        }
        triggerCacheRefresh();
        try {
            String hash = hashQueue.poll(5, TimeUnit.SECONDS);
            if (hash == null) {
                log.warn("Failed to retrieve hash within the timeout period.");
            }
            return hash;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while waiting for a hash.", e);
            return null;
        }
    }

    private void triggerCacheRefresh() {
        if (isRefreshing.compareAndSet(false, true)) {
            executorService.submit(() -> {
                try {
                    var hashes = hashRepository.getHashBatch(cacheSize);
                    hashQueue.addAll(hashes);
                    hashGenerator.generateHashes();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                } finally {
                    isRefreshing.set(false);
                }
            });
        }
    }
}