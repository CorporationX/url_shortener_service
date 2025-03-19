package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.LocalCacheProperties;
import faang.school.urlshortenerservice.config.ThreadPoolProperties;
import faang.school.urlshortenerservice.entity.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalCache {
    private final HashGenerator hashGenerator;
    private final Executor hashGeneratorExecutor;
    private final LocalCacheProperties properties;
    private final ThreadPoolProperties poolProperties;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private BlockingQueue<Hash> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(properties.getCapacity());
        fillCacheSync(properties.getCapacity());
        log.info("LocalHash initialization completed.");
    }

    public String getHash() {
        if (hashes.isEmpty()) {
            fillCacheSync(1);
        }
        if (hashes.remainingCapacity() > (properties.getCapacity() * (100 - properties.getFillPercentage()) / 100)) {
            if (isFilling.compareAndSet(false, true)) {
                log.info("Start fill cache async");
                fillCacheAsync(properties.getCapacity());
            }
        }

        Hash hash = hashes.poll();
        if (hash == null) {
            throw new RuntimeException("No hashes available in the cache");
        }
        return hash.getHash();
    }

    public void fillCacheSync(int amount) {
        List<Hash> newHashes = hashGenerator.getHashes(amount);
        if (hashes.remainingCapacity() >= amount) {
            hashes.addAll(newHashes);
        }
    }

    public void fillCacheAsync(int amount) {
        CompletableFuture.supplyAsync(() -> hashGenerator.getHashes(amount/poolProperties.getMaxPoolSize()),
                        hashGeneratorExecutor)
                .thenAccept(newHashes -> {
                    synchronized (hashes) {
                        if (hashes.remainingCapacity() >= amount/poolProperties.getMaxPoolSize()) {
                            hashes.addAll(newHashes);
                        }
                    }
                })
                .exceptionally(ex -> {
                    log.error("Failed to fill the cache with new hashes", ex);
                    isFilling.set(false);
                    return null;
                })
                .thenRun(() -> isFilling.set(false));
    }
}
