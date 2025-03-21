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
    private int maxFreeSize;


    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(properties.getCapacity());
        maxFreeSize = (properties.getCapacity() * (100 - properties.getFillPercentage()) / 100);
        fillCacheSync(properties.getCapacity());
        log.info("LocalCache initialization completed.");
    }

    public String getHash() {
        if (hashes.isEmpty()) {
            isFilling.compareAndSet(false, true);
            return hashGenerator.getHashes(1).get(0).getHash();
        }
        if (hashes.remainingCapacity() > maxFreeSize) {
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

    private void fillCacheSync(int amount) {
        List<Hash> newHashes = hashGenerator.getHashes(amount);
        queuePush(newHashes);
    }

    private void fillCacheAsync(int amount) {
        CompletableFuture.supplyAsync(() -> hashGenerator.getHashes(amount/poolProperties.getPoolSize()),
                        hashGeneratorExecutor)
                .thenAccept(this::queuePush)
                .exceptionally(ex -> {
                    isFilling.set(false);
                    throw new RuntimeException(ex);
                })
                .thenRun(() -> isFilling.set(false));
    }

    private void queuePush(List<Hash> newHashes) {
        for (Hash hash : newHashes) {
            if (!hashes.offer(hash)) {
                break;
            }
        }
    }
}
