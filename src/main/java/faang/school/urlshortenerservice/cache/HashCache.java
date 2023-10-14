package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.HashCacheException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public class HashCache {

    private final HashGenerator generator;
    private final HashRepository repository;
    private final Executor hashCacheThreadPool;
    @Value("${hash.cache.poll-timeout}")
    private long cachePollTimeout;
    @Value("${hash.cache.size}")
    private int cacheSize;
    @Value("${hash.cache.min-fill}")
    private int minFill;
    private Lock lock = new ReentrantLock();
    private BlockingQueue<String> cache;

    @PostConstruct
    private void cacheInit() {
        cache = new ArrayBlockingQueue<>(cacheSize);
        fillCache();
    }

    public String getHash() {
        if (cache.size() * 100 / cacheSize < minFill) {
            log.info("HashCache starter filling cache.");
            hashCacheThreadPool.execute(this::fillCache);
        }

        String hash;
        try {
            hash = cache.poll(cachePollTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error("Cache has interrupted while waiting method poll() ", e);
            throw new HashCacheException(e);
        }

        return hash;
    }

    public void fillCache() {
        boolean isLockAcquired = lock.tryLock();
        if (isLockAcquired) {
            log.info("Thread {} acquired HashCache lock.", Thread.currentThread().getName());
            try {
                repository.getHashBatch(cacheSize - cache.size())
                        .forEach(this::addHash);
                generator.generateBatch();
            } finally {
                lock.unlock();
                log.info("Thread {} released HashCache lock .", Thread.currentThread().getName());
            }
        }
    }

    public void addHash(String hash) {
        try {
            cache.put(hash);
        } catch (InterruptedException e) {
            log.error("Cache has interrupted while waiting method put() ", e);
            throw new HashCacheException(e);
        }
    }
}
