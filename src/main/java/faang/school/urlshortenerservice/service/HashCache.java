package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Log4j2
public class HashCache {

    @Value("${hash.cache.threshold.size}")
    private int cacheSize;
    @Value("${hash.cache.threshold.percentage}")
    private double thresholdPercentage;

    private LinkedBlockingQueue<String> hashQueue ;

    private final ExecutorService executorService;
    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);

    private final HashRepository hashRepository;

    private final HashGenerator hashGenerator;

    @PostConstruct
    public void initializeQueue() {
        this.hashQueue = new LinkedBlockingQueue<>(cacheSize);
    }

    public String getHash() {
        double threshold = cacheSize * (thresholdPercentage / 100.0);

        if (hashQueue.size() > threshold) {
            return hashQueue.poll();
        }
        triggerCacheRefresh();
        return hashQueue.poll();
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
                    e.printStackTrace();
                } finally {
                    isRefreshing.set(false);
                }
            });
        }
    }
}