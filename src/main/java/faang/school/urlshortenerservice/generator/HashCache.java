package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    @Value("${queue.capacity:1000}")
    private int capacity;
    @Value("${hash.cache.low-threshold-percentage}")
    private double lowThresholdPercentage;

    private final HashGenerator hashGenerator;
    private final Queue<String> hashes = new ArrayBlockingQueue<>(1000);
    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        hashes.addAll(hashGenerator.getHashBatch(capacity));
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
