package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.fill.percent}")
    private int fillPercent;

    //    private final Queue<String> hashes = new ArrayBlockingQueue<>(capacity);
    private final HashGenerator hashGenerator;
    private final AtomicBoolean filling = new AtomicBoolean(false);
    private final BlockingQueue<String> hashes;

    @PostConstruct
    public void init() {
        hashGenerator.generateHash();
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (capacity / hashes.size() * 100 < fillPercent &&
                filling.compareAndSet(false, true)) {
            hashGenerator.getHashesAsync(capacity)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> filling.set(false));
        }
        return hashes.poll();
    }
}