package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {
    private final HashGenerator generator;
    private final int capacity;
    private final double fillPercent;
    private final AtomicBoolean filling;
    private final Queue<String> hashes;

    public HashCache(HashGenerator generator,
                     @Value("${spring.data.capacity:10}") int capacity,
                     @Value("${spring.data.fill_percent}") double fillPercent
    ) {
        this.generator = generator;
        this.capacity = capacity;
        this.fillPercent = fillPercent;
        this.filling = new AtomicBoolean();
        this.hashes = new ArrayBlockingQueue<>(capacity);
    }

    @PostConstruct
    public void init() {
        hashes.addAll(generator.getHashBatch(capacity));
    }

    public String getHash() {
        if (getHashesCapacity() < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                generator.getHashBatchAsync(getFreeCapacity())
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return hashes.poll();
    }

    private int getFreeCapacity() {
        return (int) (capacity * (100 - fillPercent / 100.0));
    }

    private double getHashesCapacity() {
        return hashes.size() / (capacity / 100.0);
    }
}