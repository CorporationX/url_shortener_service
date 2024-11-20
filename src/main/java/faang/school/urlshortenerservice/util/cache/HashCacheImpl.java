package faang.school.urlshortenerservice.util.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Data
public class HashCacheImpl implements HashCache {

    private final Executor executorHashCache;
    private final HashCacheProperty cacheProperty;
    private final HashRepository hashRepository;
    private final HashGenerator generator;

    private int cacheRefillThreshold;
    private final Queue<String> hashQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isCacheRefilling = new AtomicBoolean(false);

    @PostConstruct
    void initialize() {
        generator.generateBatch();
        cacheRefillThreshold = (int) (cacheProperty.getMaxQueueSize() * (cacheProperty.getRefillPercent() / 100.0));
        refillCacheAsync();
    }

    @Override
    public String getHash() {
        int currentSize = hashQueue.size();

        if (currentSize < cacheRefillThreshold && isCacheRefilling.compareAndSet(false, true)) {
            refillCacheAsync();
        }

        return hashQueue.poll();
    }

    private void refillCacheAsync() {
        executorHashCache.execute(() -> {
            try {
                int currentSize = hashQueue.size();
                if (currentSize < cacheProperty.getMaxQueueSize()) {
                    int limit = cacheProperty.getMaxQueueSize() - currentSize;
                    refillingCacheWithHashes(limit);
                }
            } finally {
                isCacheRefilling.set(false);
            }
        });
    }

    private void refillingCacheWithHashes(int limit) {
        hashQueue.addAll(hashRepository.getHashBatch(limit));
        generator.generateBatch();
    }
}