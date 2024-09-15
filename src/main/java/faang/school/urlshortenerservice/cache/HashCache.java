package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private BlockingQueue<String> hashes;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    @Value("${hash.capacity}")
    private int capacity;
    @Value("${hash.lowPercentage}")
    private int lowPercentage;

    @PostConstruct
    public void fill() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGenerator.getHashes(capacity));
        log.info("Hashes have been added to cache, its size is {} now", hashes.size());
    }

    public String getHash() {
        if (hashes.size() * 100 / capacity < lowPercentage && isFilling.compareAndSet(false, true)) {
            hashGenerator.getHashesAsync(capacity).thenAccept(hashes::addAll).thenRun(()->{
                isFilling.set(false);
                log.info("Cache has been updated, its size is {}", hashes.size());
            });
        }
        return hashes.poll();
    }
}
