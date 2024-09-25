package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {

    private final HashGenerator hashGenerator;
    private final int capacity;
    private final Queue<String> hashes;
    private final int fillPercent;
    private final AtomicBoolean filling;

    public HashCache(HashGenerator hashGenerator,
                     @Value("${hash.cache.capacity:10000}") int capacity,
                     @Value("${hash.cache.fill.percent:20}") int fillPercent) {
        this.hashGenerator = hashGenerator;
        this.capacity = capacity;
        this.hashes = new ArrayBlockingQueue<>(capacity);
        this.fillPercent = fillPercent;
        this.filling = new AtomicBoolean(false);

    }

    public void init() {
        this.hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (calculatePercentage(hashes.size(), capacity) < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity - hashes.size())
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }

        return hashes.poll();
    }

    private double calculatePercentage(double part, double whole) {
        return (part / whole) * 100;
    }

}
