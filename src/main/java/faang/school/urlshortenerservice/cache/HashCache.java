package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final AtomicBoolean isFillingCache = new AtomicBoolean(false);

    @Value("${hash.cache.size:10}")
    private int cacheSize;

    @Value("${hash.cache.threshold:0.2}")
    private double threshold;

    private Queue<String> cache;

    @PostConstruct
    public void initCache() {
        cache = new ArrayBlockingQueue<>(cacheSize);
        hashGenerator.getHashBatch(cacheSize);
    }

    public String getHash() {
        if(cache.size() < (threshold * cacheSize)) {
            if(isFillingCache.compareAndSet(false, true)) {
                hashGenerator.getHashBatchAsync(cacheSize)
                        .thenAccept(newHashes -> cache.addAll(newHashes))
                        .thenRun(() -> isFillingCache.set(false));
            }
        }
        return cache.poll();
    }


}
