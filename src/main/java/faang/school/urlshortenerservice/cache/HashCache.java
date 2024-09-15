package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
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

    @Value("${hash.cache.capacity}")
    private int capacity = 999;

    @Value("${hash.cache.fill.percent}")
    private int fillPercent;


    private final AtomicBoolean filling = new AtomicBoolean(false);

    private final Queue<Hash> hashes = new ArrayBlockingQueue<>(capacity);

    @PostConstruct
    public void init() {
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public Hash getHash() {
        if (hashes.size() / (capacity / 100) < fillPercent && filling.compareAndSet(false, true)) {
            hashGenerator.getHashesAsync(capacity)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> filling.set(false));
        }
        return hashes.poll();
    }
}
