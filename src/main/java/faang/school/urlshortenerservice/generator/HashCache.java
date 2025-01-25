package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCache {

    private static final int THREAD_POOL_SIZE = 6;
    private final ConcurrentLinkedQueue<String> cache;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isRefreshing;
    private final ExecutorService executorService;

    @Value("${hash.cache.size}")
    private int maxCacheSize;

    @Value("${hash.cache.threshold.percentage}")
    private int thresholdPercentage;

    public HashCache(
            HashRepository hashRepository,
            HashGenerator hashGenerator) {
        this.cache = new ConcurrentLinkedQueue<>();
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
        this.isRefreshing = new AtomicBoolean(false);
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    @PostConstruct
    public void init() {
        executorService.submit(this::refreshCache);
    }

    public String getHash() {
        int threshold = (int) (maxCacheSize * (thresholdPercentage / 100.0));

        if (cache.size() > threshold) {
            String hash = cache.poll();
            log.debug("[{}] Returning hash from cache: {}", Thread.currentThread().getName(), hash);
            return hash;
        }

        executorService.submit(this::refreshCache);

        String hash = cache.poll();
        log.debug("[{}] Returning hash after submitting refresh: {}", Thread.currentThread().getName(), hash);
        return hash;
    }

    private void refreshCache() {
        if (isRefreshing.compareAndSet(false, true)) {
            try {
                int neededSize = maxCacheSize - cache.size();

                hashGenerator.generateBatch();
                List<String> hashes = hashRepository.getHashBatch(neededSize);

                cache.addAll(hashes);
                log.debug("[{}] Cache refreshed with {} hashes. Retrieved hashes: {} New cache size: {}",
                        Thread.currentThread().getName(), hashes, hashes.size(), cache.size());
            } finally {
                isRefreshing.set(false);
            }
        }
    }
}
