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

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (shouldRefillCache()) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        String hash = hashes.poll();
        if (hash == null) {
            hash = hashGenerator.getHashes(1).stream().findFirst() //TODO вопросики
                    .orElseThrow(() -> new IllegalStateException("No hashes available"));
            hashes.addAll(hashGenerator.getHashes(capacity - hashes.size()));
        }
        return hash;
    }

    private boolean shouldRefillCache() {
        return hashes.size() / (capacity / 100.0) < fillPercent;
    }
}
