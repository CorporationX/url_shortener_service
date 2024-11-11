package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isFillingCache = new AtomicBoolean(false);

    private final Queue<String> hashCache = new ConcurrentLinkedQueue<>();

    @Value("${hash.cache.size}")
    private int cacheSize;
    @Value("${hash.cache.low-threshold-percentage}")
    private int lowThresholdPercentage;

    public String getHash() {
        if (hashCache.size() <= (cacheSize * lowThresholdPercentage / 100) && !isFillingCache.get()) {
            refillCacheAsync();
        }
        return hashCache.poll();
    }

    @Async("cacheThreadPoolTaskExecutor")
    public void refillCacheAsync() {
        if (isFillingCache.compareAndSet(false, true)) {
            try {
                List<String> newHashes = hashRepository.getHashBatch();
                for (String hash : newHashes) {
                    if (hashCache.size() < cacheSize) {
                        hashCache.add(hash);
                    } else {
                        break;
                    }
                }
                if (hashCache.size() < cacheSize) {
                    hashGenerator.generateBatch();
                }
            } finally {
                isFillingCache.set(false);
            }
        }
    }
}
