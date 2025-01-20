package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
public class HashCache {
    private final HashGenerator hashGenerator;

    @Value("${hash.cache.capacity}")
    private int capacity;
    @Value("${hash.cache.fill-percent}")
    private double percent;

    private final AtomicBoolean filling = new AtomicBoolean(false);
    private Queue<Hash> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        try {
            hashes.addAll(hashGenerator.getHashes(capacity));
            log.info("Hashes initialized successfully.");
        } catch (Exception e) {
            log.error("Error during HashCache initialization", e);
            throw e;
        }
    }

    public Hash getHash() {
        if (hashes.size() < capacity * percent / 100.0) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity - hashes.size())
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return hashes.poll();
    }
}
