package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.entity.Hash;
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

    @Value("${hash.cache.capacity}")
    private int capacity;
    @Value("${hash.cache.min_percentage_filling}")
    private int minPercentageFilling;
    private Queue<Hash> hashes;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private final HashGenerator hashGenerator;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);

        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public Hash getHash() {
        if (percentageFilling() < minPercentageFilling) {
            if (isFilling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity - hashes.size())
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> isFilling.set(false));
            }
        }
        return hashes.poll();
    }

    private int percentageFilling() {
        return hashes.size() * 100 / capacity;
    }
}
