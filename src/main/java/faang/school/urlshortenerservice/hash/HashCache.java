package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Component
public class HashCache {

    private final HashRepository hashRepository;

    private final int cacheMinPercentageFilling;
    private final int maxSizeCache;
    private final Queue<String> cache;

    public HashCache(HashRepository hashRepository,
                     @Value("${cache.capacity}") int maxSizeCache,
                     @Value("${cache.min-percentage-filling}") int cacheMinPercentageFilling) {
        this.hashRepository = hashRepository;
        this.maxSizeCache = maxSizeCache;
        this.cacheMinPercentageFilling = cacheMinPercentageFilling;
        this.cache = new ArrayBlockingQueue<>(maxSizeCache);
    }

    public String getHash() {
        if (checkCachePercentFilling()) {
            List<String> hashes = hashRepository.getAndDeleteHashBatch(maxSizeCache - cache.size());
            cache.addAll(hashes);
        }
        return cache.peek();
    }

    private boolean checkCachePercentFilling() {
        return cache.size() < maxSizeCache / 100.0 * cacheMinPercentageFilling;
    }
}
