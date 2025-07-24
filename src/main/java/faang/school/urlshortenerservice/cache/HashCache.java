package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashCache {
    @Value(value = "${spring.hash.cache.size}")
    private int cacheSize;
    @Value(value = "${spring.hash.cache.fill}")
    private int cacheFill;

    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private final HashGenerator hashGenerator;
    private Queue<String> hashPool;

    @PostConstruct
    public void init() {
        log.info("Initializing hash cache...");
        hashPool = new ArrayBlockingQueue<>(cacheSize);
        hashPool.addAll(hashGenerator.getHashes(cacheSize));
        log.info("Hash cache initialized.");
    }

    public String getHash() {
        log.info("Getting hash from cache...");
        if (hashPool.size() * 100 / cacheSize < cacheFill
                && isFilling.compareAndSet(false, true)) {
            log.info("Cache is not full. Filling it...");
            hashGenerator.getHashesAsync(cacheSize)
                    .thenAccept(hashPool::addAll)
                    .thenRun(() -> isFilling.set(false));
            log.info("Cache filled.");
        }
        log.info("Got hash from cache.");
        return hashPool.poll();
    }
}