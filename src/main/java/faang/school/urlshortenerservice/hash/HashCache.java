package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.publisher.LowCachePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class HashCache {

    private final HashProperties hashProperties;
    private final LowCachePublisher lowCachePublisher;
    private final Queue<String> hashCache;
    private final AtomicBoolean isUpdating;
    private final AtomicInteger pollCounter;

    public HashCache(LowCachePublisher lowCachePublisher,
                     HashProperties hashProperties) {
        this.lowCachePublisher = lowCachePublisher;
        this.hashProperties = hashProperties;
        this.hashCache = new ArrayBlockingQueue<>(hashProperties.getCacheCapacity(), true);
        this.isUpdating = new AtomicBoolean(false);
        this.pollCounter = new AtomicInteger(hashProperties.getCacheCapacity());
    }

    public String getHash() {
        String hash = hashCache.poll();
        if (Objects.isNull(hash)) {
            log.error("Hash cache is empty");
            throw new IllegalStateException("Hash cache is empty");
        }

        int lowThreshold = getLowThreshold();
        int remaining = pollCounter.decrementAndGet();
        if (remaining < lowThreshold && isUpdating.compareAndSet(false, true)) {
            lowCachePublisher.publishEvent();
        }

        log.debug("Get hash: {} | cache_size = {}", hash, remaining);
        return hash;
    }

    public void setHashBatch(List<String> hashes) {
        hashCache.addAll(hashes);
        pollCounter.set(hashes.size());
        isUpdating.set(false);
    }

    private int getLowThreshold() {
        return (int) (hashProperties.getCacheCapacity() * hashProperties.getLowThresholdFactor());
    }
}
