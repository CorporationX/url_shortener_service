package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashGenerator hashGenerator;

    @Qualifier("hashCacheExecutor")
    private final Executor hashCacheExecutor;

    @Value("${app.hash-cache.size}")
    private int capacity;

    @Value("${app.hash-cache.refill-threshold}")
    private double refillThreshold;

    private BlockingQueue<String> hashes;

    private final AtomicBoolean loadingInProgress = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        try {
            List<String> initialHashes = hashGenerator.getHashBatch(capacity);
            hashes.addAll(initialHashes);
            log.info("Hash cache initialized with {} hashes", initialHashes.size());
        } catch (Exception e) {
            log.error("Failed to initialize hash cache", e);
            throw new IllegalStateException("Hash cache initialization failed", e);
        }
    }

    public String getHash() {
        log.info("Getting hash. Current cache size: {}", hashes.size());

        if (hashes.size() < capacity * refillThreshold &&
                loadingInProgress.compareAndSet(false, true)) {
            if (hashes.size() < capacity) {
                refillHash();
            } else {
                loadingInProgress.set(false);
            }
        }

        String hash = hashes.poll();

        if (hash != null) {
            return hash;
        }

        log.warn("Hash cache is empty, generating hash synchronously");
        List<String> newHashes = hashGenerator.getHashBatch(capacity);
        hash = newHashes.stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to generate hash"));

        newHashes.remove(hash);
        hashes.addAll(newHashes);

        return hash;
    }

    private void refillHash() {
        CompletableFuture.runAsync(() -> {
            try {
                int batchSize = capacity - hashes.size();
                log.debug("Refilling hash cache with {} hashes", batchSize);
                List<String> newHashes = hashGenerator.getHashBatch(batchSize);

                for (String hash : newHashes) {
                    if (!hashes.offer(hash)) {
                        log.warn("Hash cache is full — stopping refill");
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("Failed to refill hash cache", e);
            } finally {
                loadingInProgress.set(false);
            }
        }, hashCacheExecutor);
    }
}


