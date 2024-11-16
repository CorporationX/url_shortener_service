package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
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
    @Value("${hash.cache.capacity}")
    private int capacity;
    @Value("${hash.cache.min.percent}")
    private double minPercent;

    private Queue<String> hashQueue;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @PostConstruct()
    public void init() {
        hashQueue = new ArrayBlockingQueue<>(capacity);
        hashQueue.addAll(hashGenerator.getHashBatch(capacity));
    }

    public String getHash() {
        if (hashQueue.size() < (minPercent * capacity) / 100) {
            if (isFilling.compareAndSet(false, true)) {
                hashGenerator.getHashBatchAsync(capacity)
                        .thenAccept(newHashes -> hashQueue.addAll(newHashes))
                        .thenRun(() -> isFilling.set(false));
            }
        }
        return hashQueue.poll();
    }
}
