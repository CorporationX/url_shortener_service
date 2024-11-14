package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.config.cache.CacheProperties;
import faang.school.urlshortenerservice.service.hash.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {

    private static final double ONE_HUNDRED = 100.0;
    private final double COMMON_CAPACITY_PERCENTAGE;

    private final CacheProperties cacheProperties;
    private final HashService hashService;

    private final AtomicBoolean filling = new AtomicBoolean(false);
    private final Queue<String> hashes;

    @Autowired
    public HashCache(CacheProperties cacheProperties,
                     HashService hashService) {
        this.cacheProperties = cacheProperties;
        this.hashService = hashService;
        this.hashes = new ArrayBlockingQueue<>(cacheProperties.getCapacity());

        hashes.addAll(hashService.getHashes());
        this.COMMON_CAPACITY_PERCENTAGE = cacheProperties.getCapacity() / ONE_HUNDRED;
    }

    public String getHash() {
        double currentCapacity = hashes.size() / COMMON_CAPACITY_PERCENTAGE;

        if (currentCapacity <= cacheProperties.getMinLimitCapacity()
                && filling.compareAndSet(false, true)) {
            hashService.getHashesAsync()
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> filling.set(false));
        }

        return hashes.poll();
    }
}
