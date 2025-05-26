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

    private final HashGenerator hashGenerator;

    @Value("${hash.cache.capacity:10000}")
    private int capacity;

    @Value("${hash.cache.fill.percent:20}")
    private double fillPercent;

    private Queue<String> hashes;

    private final AtomicBoolean filling = new AtomicBoolean(false);

    private static final int MIN_CAPACITY = 100;
    private static final int MAX_CAPACITY = 1000000;
    private static final double MIN_FILL_PERCENT = 10.0;
    private static final double MAX_FILL_PERCENT = 90.0;

    @PostConstruct
    public void init() {
        validateParameters();
        hashes = new ArrayBlockingQueue<>(capacity);
        var initialHashes = hashGenerator.getHashes(capacity);
        if (initialHashes.isEmpty()) {
            throw new IllegalStateException("Failed to initialize hash cache: could not generate initial hashes");
        }
        hashes.addAll(initialHashes);
    }

    public String getHash() {
        if (shouldRefillCache()) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(newHashes -> {
                            synchronized (hashes) {
                                hashes.addAll(newHashes);
                            }
                        })
                        .thenRun(() -> filling.set(false));
            }
        }

        String hash;
        synchronized (hashes) {
            hash = hashes.poll();
            if (hash == null) {
                var newHashes = hashGenerator.getHashes(capacity);
                hash = newHashes.stream().findFirst()
                        .orElseThrow(() -> new IllegalStateException("Failed to generate hash"));
                newHashes.remove(hash); // Удаляем первый хеш, который мы вернем
                hashes.addAll(newHashes);
            }
        }
        return hash;
    }

    private boolean shouldRefillCache() {
        return hashes.size() / (capacity / 100.0) < fillPercent;
    }

    private void validateParameters() {
        if (capacity < MIN_CAPACITY || capacity > MAX_CAPACITY) {
            throw new IllegalStateException(
                    String.format("Cache capacity must be between %d and %d", MIN_CAPACITY, MAX_CAPACITY));
        }
        if (fillPercent < MIN_FILL_PERCENT || fillPercent > MAX_FILL_PERCENT) {
            throw new IllegalStateException(
                    String.format("Fill percent must be between %.1f and %.1f", MIN_FILL_PERCENT, MAX_FILL_PERCENT));
        }
    }
}