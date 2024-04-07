package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Alexander Bulgakov
 */
@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    @Value("${hash.cash.capacity:10000}")
    private int capacity;
    @Value("${hash.cash.fill.percent:20}")
    private int fillPercent;
    private Queue<String> hashes;
    private AtomicBoolean isFilling;

    @PostConstruct
    private void init() {
        isFilling = new AtomicBoolean(false);
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGenerator.getHash(capacity));
    }

    public String getHash() {
        if (getFillPercentage() < fillPercent) {
            if (isFilling.compareAndSet(false, true)) {
                hashGenerator.getHashAsync(capacity - hashes.size())
                        .thenAccept(hashes::addAll)
                        .exceptionally(e -> {
                            isFilling.set(false);
                            return null;
                        })
                        .thenRun(() -> isFilling.set(false));
            }
        }
        return hashes.poll();
    }

    private double getFillPercentage() {
        return (double) hashes.size() / capacity * 100;
    }
}
