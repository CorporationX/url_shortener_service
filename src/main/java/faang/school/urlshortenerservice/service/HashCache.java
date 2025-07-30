package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.service.config.HashConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashConfig hashConfig;
    private final HashService hashService;
    private final ThreadPoolTaskExecutor executor;
    private final AtomicInteger cacheCounter = new AtomicInteger();

    private final BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean refilling = new AtomicBoolean(false);

    public String getHash() {
        try {
            String hash = hashQueue.poll(30, TimeUnit.SECONDS);
            if(hash == null) {
                log.error("Cache refiling stopped!");
                throw new IllegalStateException("Failed to get hash from cache");
            }
            int left = cacheCounter.decrementAndGet();
            log.debug("Getting hash from cache. Left {}", left);
            startRefillIfNeeded();
            return hash;
        } catch (InterruptedException e) {
            log.error("Failed to get unused hash");
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for hash", e);
        }
    }

    public void refillCache() {
        List<String> freeHashes = hashService.getFreeHashes(hashConfig.getCache().getSize());
        hashQueue.addAll(freeHashes);
        cacheCounter.set(hashQueue.size());
        log.info("Cache updated with {} values, current size {}", hashConfig.getCache().getSize(), hashQueue.size());
    }

    @PostConstruct
    private void initCache() {
        refillCache();
    }

    private void startRefillIfNeeded() {
        int left = cacheCounter.get();
        boolean needRefilling = left < hashConfig.getCacheUpdateCount()
                && refilling.compareAndSet(false, true);
        if(needRefilling) {
            log.warn("Start refilling hash cache. Current cache size - {}, limit - {}",
                    left,
                    hashConfig.getCacheUpdateCount());
            refillCacheAsync();
        }
    }

    private void refillCacheAsync() {
        executor.submit(() -> {
            try {
                log.info("Start refilling hash cache...");
                refillCache();
            } finally {
                refilling.set(false);
            }
        });
    }
}