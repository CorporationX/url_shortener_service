package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashService hashService;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    @Value("${hash.cache.capacity:10000}")
    private int capacity;
    @Value("${hash.cache.fill-percentage:20}")
    private float fillPercentage;
    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        List<String> initialHashes = hashService.getHashes(capacity);
        hashes.addAll(initialHashes);
        log.info("Initialized cache with {} hashes", initialHashes.size());
    }

    public String getHash() {
        if (hashes.size() < (capacity * fillPercentage / 100.0)) {
            if (isFilling.compareAndSet(false, true)) {
                log.info("Cache below threshold (size={}), triggering asynchronous refill", hashes.size());
                hashService.generateBatch().thenRun(() -> {
                    List<String> newHashes = hashService.getHashes(capacity);
                    hashes.addAll(newHashes);
                    isFilling.set(false);
                    log.info("Cache asynchronously refilled with {} hashes", newHashes.size());
                });
            }
        }

        return hashes.poll();
    }
}
