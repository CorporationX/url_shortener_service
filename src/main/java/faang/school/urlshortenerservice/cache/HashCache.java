package faang.school.urlshortenerservice.cache;

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
    @Value("${hash.capacity}")
    private int capacity;
    @Value("${hash.fill_percent")
    private int fillPercent;
    private final AtomicBoolean flag = new AtomicBoolean(false);
    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (hashes.size() * 100.0 / capacity < fillPercent) {
            if (flag.compareAndSet(false, true)) {
                hashGenerator.getHash(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> flag.set(false));
            }
        }
        return hashes.poll();
    }
}