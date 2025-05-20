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

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashGenerator hashGenerator;
    @Value("${app.cache.size:10000}")
    private int capacity;

    @Value("${app.cache.refill-threshold:0.2}")
    private double refillThreshold;

    private final AtomicBoolean loadingInProgress = new AtomicBoolean(false);

    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGenerator.getHashBatch(capacity));
    }

    public String getHash() {
        log.info("Getting hash. Current size: {}", hashes.size());

        if (hashes.size() < capacity * refillThreshold && loadingInProgress.compareAndSet(false, true)) {
            hashGenerator.getHashBatchAsync(capacity)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> loadingInProgress.set(false));
        }
        String hash = hashes.poll();
        if (hash == null) {
            throw new IllegalStateException("No hashes available in cache");
        }
        return hash;
    }
}
