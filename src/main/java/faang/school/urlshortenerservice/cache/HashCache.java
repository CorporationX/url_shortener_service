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

    @Value("${spring.hash.cache.size}")
    private int cacheSize;

    @Value("${spring.hash.cache.fill}")
    private int cacheFill;

    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private final HashGenerator hashGenerator;
    private Queue<String> hashPool;

    @PostConstruct
    public void init() {
        log.info("Initializing hash cache...");
        hashPool = new ArrayBlockingQueue<>(cacheSize);
        try {
            hashPool.addAll(hashGenerator.getHashes(cacheSize));
            log.info("Hash cache initialized with {} hashes", hashPool.size());
        } catch (Exception e) {
            log.error("Failed to initialize hash cache", e);
        }
    }

    public String getHash() {
        log.info("Getting hash from cache...");
        if (hashPool.size() * 100 / cacheSize < cacheFill
                && isFilling.compareAndSet(false, true)) {

            log.info("Cache below fill threshold ({}%). Starting async refill...", cacheFill);

            hashGenerator.getHashesAsync(cacheSize)
                    .thenAccept(hashes -> {
                        hashPool.addAll(hashes);
                        log.info("Added {} hashes to cache. Current size: {}", hashes.size(), hashPool.size());
                    })
                    .whenComplete((res, ex) -> {
                        if (ex != null) {
                            log.error("Error while filling hash cache", ex);
                        } else {
                            log.info("Cache filled successfully");
                        }
                        isFilling.set(false);
                    });
        }
        return hashPool.poll();
    }
}
