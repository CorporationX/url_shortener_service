package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashLocalCache {
    private final HashService hashService;
    @Value("${hashes.local-cache.capacity}")
    private int cacheCapacity;
    @Value("${hashes.local-cache.threshold}")
    private int cacheThreshold;
    private BlockingQueue<String> cacheQueue;
    private final AtomicBoolean flag = new AtomicBoolean(false);

    public String getHash() {
        if (checkIfThresholdReached()) {
            if (flag.compareAndSet(false, true)) {
                fillCache(flag);
            }
        }
        return cacheQueue.poll();
    }

    private boolean checkIfThresholdReached() {
        double percentFull = (cacheQueue.size() * 100.0) / cacheCapacity;
        return percentFull < cacheThreshold;
    }

    private void fillCache(AtomicBoolean atomicBoolean) {
        hashService.getHashes(cacheCapacity - cacheQueue.size())
                .thenAccept(cacheQueue::addAll)
                .thenRun(() -> atomicBoolean.set(false))
                .exceptionally(e ->
                        {
                            log.error("Error while filling cache", e);
                            return null;
                        }
                );
    }

    @PostConstruct
    public void init() {
        cacheQueue = new LinkedBlockingQueue<>(cacheCapacity);
        hashService.generateHashes();
        try {
            cacheQueue.addAll(hashService.getHashes(cacheCapacity)
                    .exceptionally(e ->
                            {
                                log.error("Error while warming up cache", e);
                                return null;
                            }
                    )
                    .get());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error while warming up cache", e);
        }
    }
}