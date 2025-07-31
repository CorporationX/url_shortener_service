package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.async.AsyncHashGenerator;
import faang.school.urlshortenerservice.config.properties.HashGenerationProperties;
import faang.school.urlshortenerservice.exception.NoHashAvailableException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Data
@Slf4j
public class LocalCache {
    private final HashGenerationProperties hashGenerationProperties;
    private final HashGenerator hashGenerator;
    private final AsyncHashGenerator asyncHashGenerator;
    private final ReentrantLock lock = new ReentrantLock();

    private int queueSize;
    double criticalLoadFactor;
    private int amountToPull;

    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        this.queueSize = hashGenerationProperties.getQueueCapacity();
        this.criticalLoadFactor = hashGenerationProperties.getQueueCriticalLoad();
        this.amountToPull = hashGenerationProperties.getAmountToPull();
        this.hashes = new ArrayBlockingQueue<>(queueSize);
        hashes.addAll(hashGenerator.fetchHashes(queueSize));
    }

    public String getHash() {
        refillHashQueueIfCriticalLoad();
        return Optional.ofNullable(hashes.poll())
                .orElseThrow(() -> {
                    log.error("Hash queue is empty");
                    return new NoHashAvailableException("No hash found");
                });
    }

    private void refillHashQueueIfCriticalLoad() {
        if (hashes.size() < ((double) queueSize / 100) * criticalLoadFactor) {
            if (lock.tryLock()) {
                try {
                    asyncHashGenerator.getHashes()
                            .thenAccept(hashes::addAll)
                            .exceptionally(ex -> {
                                log.error("Failed to refill hashes", ex);
                                return null;
                            });
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}
