package faang.school.url_shortener_service.cache;

import faang.school.url_shortener_service.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.filled-percentage:20}")
    private int filledPercentage;

    private Queue<String> hashes;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        log.info("Initializing Hash Cache with capacity: {}", capacity);
        hashes = new ArrayBlockingQueue<>(capacity);
        hashGenerator.getHashesAsync(capacity).thenAccept(hashes::addAll);
        log.info("Hash Cache on init: {}", hashes);
    }

    public String getHash() {
            if (hashes.size() / (capacity / 100) < filledPercentage && isFilling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> isFilling.set(false));
            }
        log.info("Hash Cache after refill when close to empty {}", hashes);
        return hashes.poll();
    }
}