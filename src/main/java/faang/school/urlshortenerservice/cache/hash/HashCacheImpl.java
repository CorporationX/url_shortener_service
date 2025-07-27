package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.cache.HashCache;

import faang.school.urlshortenerservice.config.cache.HashCashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCacheImpl implements HashCache {
    private final ExecutorService hashCachePool;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final HashCashProperties hashCashProperties;

    private final ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();
    private final ReentrantLock lock = new ReentrantLock();

    @PostConstruct
    private void initCache() {
        cache.addAll(hashRepository.getHashBatch());
    }

    @Override
    public String getHash() {
        String hash = cache.poll();
        if (lessThenMinCachePercent()) {
            log.debug("Cache is less Then min cache percent. Will refill");
            refillCache();
        }
        return hash;
    }

    private boolean lessThenMinCachePercent() {
        return cache.size() < (hashCashProperties.getMaxCacheSize() / 100 * hashCashProperties.getMinPercent());
    }

    private void refillCache() {
        if (!lock.tryLock()) {
            log.info("Lock detected/ Refill is already in process");
            return;
        }

        try {
            log.info("Async refill process started");
            CompletableFuture.supplyAsync(hashRepository::getHashBatch, hashCachePool)
                    .thenAccept(cache::addAll)
                    .thenRunAsync(hashGenerator::generateBatch, hashCachePool)
                    .thenRun(lock::unlock)
                    .exceptionally(throwable -> {
                        log.error("Cache refill failed", throwable);
                        return null;
                    });
        } catch (Exception e) {
            log.error("Cache refill initialization failed", e);
        } finally {
            lock.unlock();
        }
    }
}