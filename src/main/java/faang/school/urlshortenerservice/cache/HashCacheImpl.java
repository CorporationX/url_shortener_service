package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCacheImpl implements HashCache {

    private final int capacity;
    private final int cacheExhaustionPercentage;

    private final HashService hashService;

    private final Queue<String> cache;
    private final AtomicBoolean isFilling;

    public HashCacheImpl(
            @Qualifier("urlHashCacheCapacity") int capacity,
            int cacheExhaustionPercentage,
            HashService hashService
    ) {
        this.capacity = capacity;
        this.cacheExhaustionPercentage = cacheExhaustionPercentage;
        this.hashService = hashService;
        this.cache = new ArrayBlockingQueue<>(capacity);
        this.isFilling = new AtomicBoolean(false);
    }

    @PostConstruct
    public void init() {
        cache.addAll(
                hashService.getHashes(capacity)
        );
    }

    @Retryable(
            retryFor = {NoSuchElementException.class},
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Override
    public String getHash() {

        if (cacheExhausted()) {
            if (isFilling.compareAndExchange(false, true)) {
                fillCacheAsync();
            }
        }

        String hash = cache.poll();

        if (hash == null) {
            log.error("Cache is empty");
            throw new NoSuchElementException("Cache is empty");
        }

        return hash;
    }

    private boolean cacheExhausted() {
        return cache.size() <= cacheExhaustionPercentage * capacity;
    }

    private void fillCacheAsync() {
        hashService.getHashesAsync(capacity)
                .thenAccept(cache::addAll)
                .thenRun(() -> isFilling.set(false));
    }
}
