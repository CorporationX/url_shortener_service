package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${hash.cache.capacity:10000}")
    private int hashCapacity;

    @Value("${hash.cache.queue-percentage}")
    private int fillPercent;

    private final HashGenerator hashGenerator;
    private final AtomicBoolean filling = new AtomicBoolean(false);
    private LinkedBlockingQueue<String> hashes;

    @PostConstruct
    public void init() {
        hashes = new LinkedBlockingQueue<>(hashCapacity);
        hashes.addAll(hashGenerator.getHashes(hashCapacity));
    }

    public String getHash() {
        if (getCurrentFillPercent() < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(hashCapacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return hashes.poll();
    }

    private int getCurrentFillPercent() {
        return hashes.size() / (hashCapacity / 100);
    }
}
