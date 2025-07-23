package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.HashCacheConfig;
import faang.school.urlshortenerservice.entity.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCacheImpl implements HashCache {

    private final HashGenerator hashGenerator;
    private final HashCacheConfig hashCacheConfig;

    private final AtomicBoolean isGenerating = new AtomicBoolean(false);

    private Queue<Hash> freeHashes;

    @PostConstruct
    public void init() {
        this.freeHashes = new ArrayBlockingQueue<>(hashCacheConfig.getCapacity());
        freeHashes.addAll(hashGenerator.getStartHashes());
    }

    @Override
    @Async("value = hashCacheExecutor")
    public Hash getHash() {
        Hash hash = freeHashes.poll();

        if (freeHashes.size() < hashCacheConfig.getCapacity() * hashCacheConfig.getThresholdPercent()) {
            if (isGenerating.compareAndSet(false, true)) {
                fillHashesAsync();
            }
        }
        return hash;
    }

    private void fillHashesAsync() {
        hashGenerator.getHashBatch().whenComplete((hashes, ex) -> {
            try {
                if (ex != null) {
                    log.error("Error during hash generation", ex);
                } else if (hashes != null && !hashes.isEmpty()) {
                    freeHashes.addAll(hashes);
                }
                if (freeHashes.size() < hashCacheConfig.getCapacity() * (1 - hashCacheConfig.getThresholdPercent())) {
                    fillHashesAsync();
                }
            } finally {
                if (freeHashes.size() >= hashCacheConfig.getCapacity() * (1 - hashCacheConfig.getThresholdPercent())) {
                    isGenerating.set(false);
                }
            }
        });
    }

}
