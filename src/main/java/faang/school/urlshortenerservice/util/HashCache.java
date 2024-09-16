package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final ExecutorService hashCacheThreadPool;

    @Value("${url.hash.cache.size}")
    private int cacheSize;
    @Value("${url.hash.cache.threshold-percent}")
    private int thresholdPercent;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);
    private final ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();

    @PostConstruct
    public void init() {
        log.info("Pre-generating first batch of hashes");
        refillCache();
        log.info("Hash cache initialized with {} hashes", cache.size());
    }

    public String getHash() {
        if (shouldRefill()) {
            triggerRefill();
        }
        return cache.poll();
    }

    private boolean shouldRefill() {
        return cache.size() < cacheSize * thresholdPercent / 100;
    }

    private void triggerRefill() {
        if (isRefilling.compareAndSet(false, true)) {
            hashCacheThreadPool.submit(this::refillCache);
        }
    }

    private void refillCache() {
        try {
            var hashes = hashRepository.getHashBatch();
            cache.addAll(hashes);
            hashGenerator.generateBatch();
        } catch (Exception e) {
            log.error("Error while refilling cache. Next refill is going to happen sooner", e);
        } finally {
            isRefilling.set(false);
        }
    }
}
