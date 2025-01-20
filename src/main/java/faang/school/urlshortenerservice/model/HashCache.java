package faang.school.urlshortenerservice.model;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCache {

    private final ConcurrentLinkedQueue<String> cache;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isRefreshing;
    private final Executor executor;

    @Value("${hash.cache.size}")
    private int maxCacheSize;

    @Value("${hash.cache.threshold.percentage}")
    private int thresholdPercentage;

    public HashCache(HashRepository hashRepository, HashGenerator hashGenerator) {
        this.cache = new ConcurrentLinkedQueue<>();
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
        this.isRefreshing = new AtomicBoolean(false);
        this.executor = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    public void init() {
        executor.execute(this::refreshCache);
    }

    public synchronized String getHash() {
        int threshold = (int) (maxCacheSize * (thresholdPercentage / 100.0));
        if (cache.size() > threshold) {
            return cache.poll();
        } else if (isRefreshing.compareAndSet(false, true)) {
            executor.execute(this::refreshCache);
        }
        return cache.poll();
    }

    private void refreshCache() {
        try {
            List<String> hashes = hashRepository.getHashBatch(maxCacheSize - cache.size());
            cache.addAll(hashes);
            if (hashes.isEmpty()) {
                hashGenerator.generateBatch();
            }
        } finally {
            isRefreshing.set(false);
        }
    }
}
