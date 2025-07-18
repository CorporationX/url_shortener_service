package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${hash.cache.min_generated_percentage}")
    private int minPercentage;
    @Value("${hash.cache.max_size}")
    private int maxSize;
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
        return cache.size() < (maxSize * minPercentage / 100);
    }

    private void refillCache() {
        if (lock.isLocked()) {
            log.info("Minimum count of hashes reached. Lock detected, refilling on process.");
            return;
        }

        lock.lock();
        CompletableFuture.supplyAsync(hashRepository::getHashBatch, hashCachePool)
                .thenAccept(cache::addAll)
                .thenRun(lock::unlock);
        CompletableFuture.runAsync(hashGenerator::generateBatch, hashCachePool);

        log.info("Minimum count of hashes reached. Hashes refilling started.");
    }
}
