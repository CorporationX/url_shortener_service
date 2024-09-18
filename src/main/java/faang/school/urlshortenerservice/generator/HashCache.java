package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    @Value("${hash.cache.queue.capacity}")
    private int capacity;
    @Value("${hash.cache.low-threshold-percentage}")
    private double lowThresholdPercentage;

    private final HashGenerator hashGenerator;
    private Queue<String> hashes;
    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        List<String> generatedHashes = hashGenerator.getHashBatch(capacity);
        try {
            hashes.addAll(generatedHashes);
        } catch (IllegalStateException e) {
            log.error("Error initializing HashCache: Queue is full", e);
        }
    }

    public String getHash() {
        if (!(hashes.size() > capacity * lowThresholdPercentage / 100)) {
            triggerRefresh();
        }
        String hash = hashes.poll();
        log.info("Retrieved hash from cache: {}", hash);
        return hash;
    }

    private void triggerRefresh() {
        if (isRefreshing.compareAndSet(false, true)) {
            hashGenerator.getHashBatchAsync(capacity)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> isRefreshing.set(false));
        }
    }
}
