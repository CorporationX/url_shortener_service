package faang.school.urlshortenerservice.hashservice;

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

    @Value("${cache.cache-capacity}")
    private int capacity;

    @Value("${cache.fill-percent}")
    private int fillPercent;

    private final AtomicBoolean filling = new AtomicBoolean(false);
    private Queue<String> cacheHashes;

    @PostConstruct
    public void init() {
        cacheHashes = new ArrayBlockingQueue<>(capacity);
        cacheHashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (cacheHashes.size() / (capacity / 100.0) < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity - cacheHashes.size())
                        .thenAccept(cacheHashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return cacheHashes.poll();
    }
}
