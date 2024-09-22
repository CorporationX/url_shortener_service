package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;

    @Value("${hash.cache.capacity:10000}")
    private int capacity;

    @Value("${hash.cache.fill-percentage:20}")
    private int fillPercentage;

    private Queue<String> hashes;

    private final AtomicBoolean isFilling = new AtomicBoolean();

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        fillQueue(capacity);
    }

    @Async("hashCacheExecutor")
    public CompletableFuture<String> getHash() {
        double currentFillPercentage = ((double) hashes.size() / capacity) * 100.0;
        if (currentFillPercentage < fillPercentage) {
            if (isFilling.compareAndSet(false, true)) {
                fillQueue(capacity);
            }
        }
        return CompletableFuture.completedFuture(hashes.poll());
    }

    private void fillQueue(int size) {
        hashGenerator.generateBatch(size)
            .thenAccept(hashes::addAll)
            .thenRun(() -> isFilling.set(false));
    }
}