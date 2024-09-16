package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.entity.Hash;
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

    @Value("${hash.cache.capacity}")
    private int capacity;
    @Value("${hash.cache.min_percentage_filling}")
    private int minPercentageFilling;
    private Queue<Hash> cache;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private final HashGenerator hashGenerator;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(capacity);

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
