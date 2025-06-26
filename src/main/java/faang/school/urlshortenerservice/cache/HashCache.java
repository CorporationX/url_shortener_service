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

    @Value("${hash.cache.min-capacity:100}")
    private int minCapacity;

    @Value("${hash.cache.max-capacity:1000000}")
    private int maxCapacity;

    @Value("${hash.cache.min-fill-percent:10.0}")
    private double minFillPercent;

    @Value("${hash.cache.max-fill-percent:90.0}")
    private double maxFillPercent;

    @PostConstruct
    public void init() {
        validateParameters();
        hashes = new ArrayBlockingQueue<>(capacity);

        var existingHashes = hashGenerator.getHashes(capacity);
        int remainingCapacity = capacity - existingHashes.size();
        
        if (!existingHashes.isEmpty()) {
            hashes.addAll(existingHashes);
        }

        if (remainingCapacity > 0) {
            var additionalHashes = hashGenerator.getHashes(remainingCapacity);
            if (additionalHashes.isEmpty() && hashes.isEmpty()) {
                throw new IllegalStateException("Failed to initialize hash cache: could not generate initial hashes");
            }
            hashes.addAll(additionalHashes);
        }
    }

    public String getHash() {
        if (shouldRefillCache()) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity)
                    .thenAccept(newHashes -> {
                        hashes.addAll(newHashes);
                    });
            }
        }

        String hash = hashes.poll();
        if (hash == null) {
            var newHashes = hashGenerator.getHashes(capacity);
            hash = newHashes.stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("Failed to generate hash"));
            newHashes.remove(hash);
            hashes.addAll(newHashes);
        }
        return hash;
    }

    private boolean shouldRefillCache() {
        return hashes.size() / (capacity / 100.0) < fillPercent;
    }

    private void validateParameters() {
        if (capacity < minCapacity || capacity > maxCapacity) {
            throw new IllegalStateException(
                    String.format("Cache capacity must be between %d and %d", minCapacity, maxCapacity));
        }
        if (fillPercent < minFillPercent || fillPercent > maxFillPercent) {
            throw new IllegalStateException(
                    String.format("Fill percent must be between %.1f and %.1f", minFillPercent, maxFillPercent));
        }
    }
}