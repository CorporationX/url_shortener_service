package faang.school.urlshortenerservice.hash_generator;

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

        log.info("Hash has been heated. Records count: {}", hashes.size());
    }

    public String getHash() {
        if (hashes.size() * 100.0 / storageCapacity < minThresholdPercent
                && !isFillingHash.compareAndExchange(false, true)) {
            log.debug("In-memory cache is almost empty {}%. Starting filling",
                    hashes.size() * 100.0 / storageCapacity);

            hashGenerator.getHashesAsync(storageCapacity)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> {
                        log.debug("New hashes have been added to in-memory cache");
                        isFillingHash.set(false);
                    });
        }

        return hashes.poll();
    }
}
