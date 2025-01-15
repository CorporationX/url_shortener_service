package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.properties.short_url.HashProperties;
import faang.school.urlshortenerservice.util.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCache {

    private final HashGenerator hashGenerator;
    private final HashProperties hashProperties;
    private final Queue<String> freeHashesQueue;
    private final AtomicBoolean isQueueBeingUpdated = new AtomicBoolean(false);

    public HashCache(HashGenerator hashGenerator, HashProperties hashProperties) {
        this.hashGenerator = hashGenerator;
        this.hashProperties = hashProperties;
        freeHashesQueue = new ArrayBlockingQueue<>(hashProperties.getCacheCapacity());
    }

    @PostConstruct
    public void initQueue() {
        log.info("Initializing hash queue...");
        List<String> freeHashes = hashGenerator.getHashes(hashProperties.getCacheCapacity());
        freeHashesQueue.addAll(freeHashes);
        log.info("Initializing hash queue was finished");
    }

    public String getHash() {
        if (freeHashesQueue.size() < hashProperties.getCacheCapacity() * hashProperties.getMinPercentageThreshold() / 100) {
            if (isQueueBeingUpdated.compareAndSet(false, true)) {
                log.info("Queue size below threshold. Triggering async replenishment.");
                hashGenerator.getHashesAsync(hashProperties.getCacheCapacity() - freeHashesQueue.size())
                        .thenRun(hashGenerator::generateBatch);
            }
        }
        return freeHashesQueue.poll();
    }
}
