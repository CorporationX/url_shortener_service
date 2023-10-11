package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.HashCacheException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashGenerator generator;
    private final HashRepository repository;
    @Value("${hash.cache.size}")
    private int CACHE_SIZE;
    @Value("${hash.cache.min-fill}")
    private int MIN_FILL;
    private Lock lock = new ReentrantLock();
    private BlockingQueue<String> cache = new ArrayBlockingQueue<>(CACHE_SIZE);

    public HashCache(HashGenerator generator, HashRepository repository, int CACHE_SIZE, int MIN_FILL) {
        this.generator = generator;
        this.repository = repository;
        this.CACHE_SIZE = CACHE_SIZE;
        this.MIN_FILL = MIN_FILL;
    }

    @PostConstruct
    private void cacheInit() {
        fillCache();
    }

    public String getHash() {
        if (cache.size() / CACHE_SIZE < MIN_FILL / 100) {
            log.info("HashCache starter filling cache.");
            fillCache();
        }

        String hash;
        try {
            hash = cache.take();
        } catch (InterruptedException e) {
            log.error("Cache has interrupted while waiting method take()");
            throw new HashCacheException(e);
        }

        return hash;
    }

    @Async("hashCacheThreadPool")
    public void fillCache() {
        boolean isLockAcquired = lock.tryLock();
        if (isLockAcquired) {
            log.info("Thread {} acquired HashCache lock.", Thread.currentThread().getName());
            try {
                repository.getHashBatch(CACHE_SIZE - cache.size())
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
            log.error("Cache has interrupted while waiting method put()");
            throw new HashCacheException(e);
        }
    }
}
