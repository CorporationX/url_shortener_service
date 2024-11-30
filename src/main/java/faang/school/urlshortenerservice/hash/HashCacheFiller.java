package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCacheFiller {
    private final HashService hashService;
    private final HashCache hashCache;
    private final AtomicBoolean isUpdating;

    public HashCacheFiller(HashService hashService, HashCache hashCache) {
        this.hashService = hashService;
        this.hashCache = hashCache;
        isUpdating = new AtomicBoolean(false);
    }

    @Value("${hash.low-threshold-cache-size}")
    private int lowThresholdCacheSize;

    @PostConstruct
    public void init() {
        if (isUpdating.compareAndSet(false, true)) {
            hashService.generateHashIfNecessary();
            List<String> hashBatch = hashService.pollHashBatch();
            hashCache.setHashBatch(hashBatch);
            isUpdating.set(false);
            log.info("Hash cache has been updated");
        }
    }

    @Async
    public void fillCacheIfNecessary() {
        if (isUpdating.compareAndSet(false, true)
                && hashCache.getSize() < lowThresholdCacheSize) {
            List<String> hashBatch = hashService.pollHashBatch();
            hashCache.setHashBatch(hashBatch);
            hashService.generateHashIfNecessary();
            isUpdating.set(false);
        }
    }
}
