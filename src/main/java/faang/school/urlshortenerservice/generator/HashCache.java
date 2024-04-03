package faang.school.urlshortenerservice.generator;

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
    @Value("${hash.check-percent}")
    private int percent;
    private final AtomicBoolean filling = new AtomicBoolean(false);
    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (hashes.size() * 100 / capacity < percent) {
            generateHashCache();
        }
        return hashes.poll();
    }

    public void generateHashCache() {
        if (filling.compareAndSet(false, true)) {
            hashGenerator.getHashesAsync(capacity)
                    .thenApply(hashes::addAll)
                    .thenRun(() -> filling.set(false));
        }
    }
}