package faang.school.urlshortenerservice.local.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.HashGeneratorTransactionalService;
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
public class LocalCache {
    @Value("${hash.cache.capacity:110_000}")
    private int capacity;

    @Value("${hash.batch-size:5_000}")
    private int batchSize;

    @Value("${hash.cache.fill-percent:20}")
    private int fillPercent;

    private final HashGenerator hashGenerator;
    private final HashGeneratorTransactionalService hashGeneratorTransactionalService;
    private Queue<String> hashQueue;
    private final AtomicBoolean filling = new AtomicBoolean(false);

    @PostConstruct
    public void setup() {
        this.hashQueue = new ArrayBlockingQueue<>(capacity);
        log.debug("Hash queue initialized with capacity: {}", capacity);
    }

    public void init() {
        if (filling.compareAndSet(false, true)) {
            try {
                log.debug("Synchronous cache population with the first batch");
                hashQueue.addAll(hashGeneratorTransactionalService.generateInitBatch(batchSize));
                log.debug("Start of asynchronous cache replenish");
                replenishHashQueueAsync();
            } catch (Exception exception) {
                log.error("Error during cache population", exception);
                filling.set(false);
            }
        }
    }

    public String getHash() {
        if (filling.compareAndSet(false, true) && getHashQueueLoadPercentage() < fillPercent) {
            log.debug("Cache below fill percent. Starting asynchronous replenish.");
            replenishHashQueueAsync();
        }
        String hash = hashQueue.poll();
        if (hash == null) {
            log.warn("Hash queue is empty.");
        }
        return hash;
    }

    public void replenishHashQueueAsync() {
        hashGenerator.getHashesAsync(capacity - hashQueue.size(), batchSize)
                .thenAccept(newHashes -> {
                    if (newHashes != null) {
                        hashQueue.addAll(newHashes);
                        log.debug("Replenished cache with {} new hashes.", newHashes.size());
                    }
                })
                .exceptionally(ex -> {
                    log.error("Error replenishing hash queue", ex);
                    return null;
                })
                .whenComplete((result, throwable) -> filling.set(false));
    }

    private int getHashQueueLoadPercentage() {
        return hashQueue.size() * 100 / capacity;
    }

    public boolean isCacheHasHashes() {
        return !hashQueue.isEmpty();
    }
}
