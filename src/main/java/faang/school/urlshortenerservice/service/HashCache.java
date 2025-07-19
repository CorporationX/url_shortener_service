package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.config.HashConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashConfig hashConfig;
    private final HashRepository hashRepository;
    private final HashService hashService;
    private final ThreadPoolTaskExecutor executor;

    private final BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean refilling = new AtomicBoolean(false);

    public String getHash() {
        try {
            log.debug("Getting hash from cache");
            String hash = hashQueue.take();
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
        log.info("Cache updated with {} values", hashConfig.getCache().getSize());
    }


    @PostConstruct
    private void initCache() {
        long currentFreeHashes = hashRepository.count();
        if(currentFreeHashes < hashConfig.getCache().getSize()) {
            hashService.refillHashStorage();
        }
        refillCache();
    }

    private void startRefillIfNeeded() {
        boolean needRefilling = hashQueue.size() < hashConfig.getCacheUpdateCount()
                && refilling.compareAndSet(false, true);
        if(needRefilling) {
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