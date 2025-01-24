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

    private static final int HASH_LENGTH = 6;
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
        this.executorService = Executors.newFixedThreadPool(HASH_LENGTH);
    }

    @PostConstruct
    public void init() {
        log.info("[{}] Initializing cache...",
                Thread.currentThread().getName());
        executorService.submit(this::refreshCache);
    }

    public String getHash() {
        log.info("[{}] getHash() called. Current cache size: {}",
                Thread.currentThread().getName(), cache.size());
        int threshold = (int) (maxCacheSize * (thresholdPercentage / 100.0));

        if (cache.size() > threshold) {
            String hash = cache.poll();
            log.info("[{}] Returning hash from cache: {}",
                    Thread.currentThread().getName(), hash);
            return hash;
        }

        log.info("[{}] Cache size below threshold. Submitting refresh task.",
                Thread.currentThread().getName());
        executorService.submit(this::refreshCache);

        String hash = cache.poll();
        log.info("[{}] Returning hash after submitting refresh: {}",
                Thread.currentThread().getName(), hash);
        return hash;
    }

    private void refreshCache() {
        log.info("[{}] Attempting to refresh cache. Current cache size: {}",
                Thread.currentThread().getName(), cache.size());

        if (isRefreshing.compareAndSet(false, true)) {
            try {
                log.info("[{}] Cache refresh started...",
                        Thread.currentThread().getName());
                int neededSize = maxCacheSize - cache.size();
                log.info("[{}] Fetching {} hashes from repository.",
                        Thread.currentThread().getName(), neededSize);

                log.warn("[{}] Generating new batch.",
                        Thread.currentThread().getName());
                hashGenerator.generateBatch();
                List<String> hashes = hashRepository.getHashBatch(neededSize);

                log.info("[{}] Retrieved hashes: {}",
                        Thread.currentThread().getName(), hashes);
                cache.addAll(hashes);
                log.info("[{}] Cache refreshed with {} hashes. New cache size: {}",
                        Thread.currentThread().getName(), hashes.size(), cache.size());
            } finally {
                isRefreshing.set(false);
            }
        }
    }
}
