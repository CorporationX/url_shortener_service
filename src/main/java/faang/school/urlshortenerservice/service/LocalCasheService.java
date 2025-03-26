package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Component
public class LocalCasheService {
    private final HashGeneratorService hashGeneratorService;

    @Value("${hash.cache.fill-percent:20}")
    private int fillPercent;

    private AtomicBoolean filling = new AtomicBoolean(false);
    @Value("${hash.cache.capacity:10000}")
    private int capacity;

    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        if (capacity <= 0) {
            throw new IllegalStateException("Capacity must be positive");
        }
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGeneratorService.getHashes(capacity));
    }

    public String getHash() {
        if (shouldRefillHashes()){
            if (filling.compareAndSet(false, true)) {
                hashGeneratorService.getHashesAsync(capacity).thenAccept(hashes::addAll)
                                .thenRun(() -> filling.set(false));
            }
        }
        return hashes.poll();
    }

    boolean shouldRefillHashes() {
        int currentFillPercent = (hashes.size() * 100) / capacity;
        return currentFillPercent < fillPercent;
    }
}
