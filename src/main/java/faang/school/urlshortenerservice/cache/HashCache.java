package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCache {
    private final int capacity;
    private final int minPercentageFilling;
    private final Queue<Hash> cache;
    private final AtomicBoolean isFilling;
    private final HashGenerator hashGenerator;

    public HashCache(@Value("${hash.cache.capacity}") int capacity,
                     @Value("${hash.cache.min_percentage_filling}") int minPercentageFilling,
                     HashGenerator hashGenerator) {
        this.capacity = capacity;
        this.minPercentageFilling = minPercentageFilling;
        this.cache = new ArrayBlockingQueue<>(capacity);
        this.isFilling = new AtomicBoolean(false);
        this.hashGenerator = hashGenerator;
    }

    @PostConstruct
    public void init() {
        log.info("Initializing hash cache");
        cache.addAll(hashGenerator.getHashes(capacity));
    }

    public Hash getHash() {
        if (percentageFilling() < minPercentageFilling) {
            if (isFilling.compareAndSet(false, true)) {
                log.info("Filling hash cache");
                hashGenerator.getHashesAsync(capacity - cache.size())
                        .thenAccept(cache::addAll)
                        .thenRun(() -> isFilling.set(false));
            }
        }
        return cache.poll();
    }

    private int percentageFilling() {
        return cache.size() * 100 / capacity;
    }
}
