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
    @Value("${hash.cache.capacity:10000}")
    private int capacity;

    @Value("${hash.cache.filled-percentage:20}")
    private int FILLED_PERCENTAGE;

    private Queue<String> hashes;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashGenerator.getHashes(capacity).thenAccept(hashes::addAll);
    }

    public String getHash() {
        if (hashes.size() / (capacity / 100) < FILLED_PERCENTAGE && isFilling.compareAndSet(false, true)) {
            hashGenerator.getHashes(capacity)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> isFilling.set(false));
        }
        return hashes.poll();
    }
}
