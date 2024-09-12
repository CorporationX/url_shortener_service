package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${queue.capacity}")
    private int capacity;
    @Value("${hash.cache.low-threshold-percentage}")
    private double lowThresholdPercentage;

    private final HashGenerator hashGenerator;
    private final Queue<String> hashes = new ArrayBlockingQueue<>(capacity);
    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        hashes.addAll(hashGenerator.getHashBatch(capacity));
    }

    public String getHash() {
        if (!(hashes.size() > capacity * lowThresholdPercentage / 100)) {
            triggerRefresh();
        }
        return hashes.poll();
    }

    private void triggerRefresh() {
        if (isRefreshing.compareAndSet(false, true)) {
            hashGenerator.getHashBatchAsync(capacity)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> isRefreshing.set(false));
        }
    }
}
