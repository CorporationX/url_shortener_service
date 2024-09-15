package faang.school.urlshortenerservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final AsyncExecutorForHashCash asyncEx;

    @Value("${hash.cache.size}")
    private int maxCacheSize;
    @Value("${cache.min_threshold}")
    private int minThreshold;
    @Value("${spring.data.batch_size}")
    private int batchSize;

    ConcurrentLinkedQueue<String> hashCache = new ConcurrentLinkedQueue<>();

    public String getCache() {
        if(hashCache.size() < minThreshold * maxCacheSize / 100) {
            asyncEx.exclusiveTransferHashBatch(batchSize, hashCache);
            asyncEx.asyncGenerateBatch();
        }
        return hashCache.poll();
    }
}
