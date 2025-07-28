package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.config.HashCacheConfig;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.HashCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCacheImpl implements HashCache {

    private final HashCacheService hashCacheService;
    private final HashCacheConfig hashCacheConfig;

    private final AtomicBoolean isGenerating = new AtomicBoolean(false);

    private Queue<Hash> freeHashes;

    @PostConstruct
    public void init() {
        this.freeHashes = new ArrayBlockingQueue<>(hashCacheConfig.getCapacity());
        offerHashesToQueue(hashCacheService.getStartHashes());
    }

    @Override
    @Async("hashCacheExecutor")
    public CompletableFuture<String> getHashAsync() {
        Hash hash = freeHashes.poll();

        if (freeHashes.size() < hashCacheConfig.getCapacity() * hashCacheConfig.getThresholdPercent()) {
            if (isGenerating.compareAndSet(false, true)) {
                fillHashesAsync();
            }
        }
        return CompletableFuture.completedFuture(hash.getHash());
    }

    private void fillHashesAsync() {
        if (!isGenerating.compareAndSet(false, true)) {
            return;
        }
        hashCacheService.getHashBatchAsync().whenComplete((hashes, exception) -> {
            try {
                if (exception != null) {
                    log.error("Error during hash generation", exception);
                } else if (hashes != null && !hashes.isEmpty()) {
                    offerHashesToQueue(hashes);
                }
            } finally {
                isGenerating.set(false);
            }
        });
    }

    private void offerHashesToQueue (List<Hash> hashes){
        for (Hash hash : hashes) {
            if (!freeHashes.offer(hash)) {
                log.warn("Queue is full, stopping adding hashes");
                break;
            }
        }

    }
}