package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.LocalCacheProperties;
import faang.school.urlshortenerservice.generator.HashGenerator;
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
    private final Executor taskExecutor;
    private final LocalCacheProperties properties;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private BlockingQueue<String> hashes;
    private int hashesAmountThreshold;


    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(properties.getCapacity());
        hashesAmountThreshold = (properties.getCapacity() * (100 - properties.getFillPercentage()) / 100);
        fillCacheSync(properties.getCapacity());
        log.info("LocalCache initialization completed.");
    }

    public String getHash() {
        if (hashes.isEmpty()) {
            isFilling.compareAndSet(false, true);
            return hashGenerator.getHashes(1).get(0);
        }
        if (hashes.remainingCapacity() > hashesAmountThreshold) {
            if (isFilling.compareAndSet(false, true)) {
                log.info("Start fill cache async");
                fillCacheAsync(properties.getCapacity());
            }
        }

        String hash = hashes.poll();
        if (hash == null) {
            throw new RuntimeException("No hashes available in the cache");
        }
        return hash;
    }

    private void fillCacheSync(int amount) {
        List<String> newHashes = hashGenerator.getHashes(amount);
        queuePush(newHashes);
    }

    private void fillCacheAsync(int amount) {
        CompletableFuture.supplyAsync(() -> hashGenerator.getHashes(amount), taskExecutor)
                .thenAccept(this::queuePush)
                .exceptionally(ex -> {
                    isFilling.set(false);
                    throw new RuntimeException(ex);
                })
                .thenRun(() -> isFilling.set(false));
    }

    private void queuePush(List<String> newHashes) {
        log.info("Push {}", newHashes.size());
        for (String hash : newHashes) {
            if (!hashes.offer(hash)) {
                break;
            }
        }
    }
}
