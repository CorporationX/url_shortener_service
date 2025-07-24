package faang.school.urlshortenerservice.hash.cache;

import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.config.hash.CacheProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.hash.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@AllArgsConstructor
public class HashCacheImpl implements HashCache {
    private final ExecutorService hashCachePool;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final CacheProperties cacheProperties;

    private final ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public String getHash() {
        if (isMinSizeReached()) {
            refillCache();
        }

        return cache.poll();
    }

    private boolean isMinSizeReached() {
        return cache.size() < (cacheProperties.maxSize() * cacheProperties.minGeneratedPercentage() / 100);
    }

    private void refillCache() {
        if (lock.isLocked()) {
            log.info("Minimum count of hashes reached. Lock detected, refilling on process.");
            return;
        }

        lock.lock();
        CompletableFuture.runAsync(this::addHashes, hashCachePool);
        CompletableFuture.runAsync(hashGenerator::generateBatch, hashCachePool);

        log.info("Minimum count of hashes reached. Hashes refilling started.");
    }

    private void addHashes() {
        try {
            cache.addAll(hashRepository.getHashBatch());
        } finally {
            lock.unlock();
        }
    }

    @PostConstruct
    private void postConstruct() {
        this.refillCache();
    }
}
