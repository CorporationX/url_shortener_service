package faang.school.urlshortenerservice.hash_generator;

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

    @Value("${hash-cache.capacity:500}")
    @SuppressWarnings("unused")
    private int storageCapacity;

    @Value("${hash-cache.min-threshold-percent:20}")
    @SuppressWarnings("unused")
    private int minThresholdPercent;

    private Queue<String> hashes;
    private final AtomicBoolean isFillingHash = new AtomicBoolean();

    @PostConstruct
    @SuppressWarnings("unused")
    public void init() {
        hashes = new ArrayBlockingQueue<>(storageCapacity);
        hashes.addAll(hashGenerator.getHashes(storageCapacity));
    }

    public String getHash() {
        if (hashes.size() * 100.0 / storageCapacity < minThresholdPercent
                && !isFillingHash.compareAndExchange(false, true)) {
            hashGenerator.getHashesAsync(storageCapacity)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> isFillingHash.set(false));
        }

        return hashes.poll();
    }
}
