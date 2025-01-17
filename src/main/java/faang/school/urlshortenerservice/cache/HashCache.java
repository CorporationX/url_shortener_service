package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
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
public class HashCache {

    private final HashGenerator hashGenerator;

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.fill-percentage}")
    private int fillPercentage;

    private final AtomicBoolean filling = new AtomicBoolean(false);

    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        this.hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGenerator.getHashes(capacity));
        log.info("Current queue size is : " + hashes.size());
    }

    public String getHash() {
        if (hashes.size() / (capacity / 100.0) < fillPercentage) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity).thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        log.info("Queue size after polling is : " + hashes.size());
        return hashes.poll();
    }

}
