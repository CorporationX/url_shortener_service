package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class HashFacade {

    private final FreeHashGenerator freeHashGenerator;
    private final LocalCacheService localCacheService;
    private final FreeHashRepository freeHashRepository;
    private final Executor executor;
    private final AtomicBoolean lock = new AtomicBoolean(false);

    @Value("${shortener.hash-pool.max-postgres-capacity}")
    private long maxDbCapacity;

    @Value("${shortener.hash-pool.max-cache-capacity}")
    private int maxCacheCapacity;

    @Value("${shortener.hash-pool.refill-threshold-percent}")
    private int refillThresholdPercent;

    public HashFacade(FreeHashGenerator freeHashGenerator,
                      LocalCacheService localCacheService,
                      FreeHashRepository freeHashRepository,
                      @Qualifier("hashServiceExecutor") Executor executor) {

        this.freeHashGenerator = freeHashGenerator;
        this.localCacheService = localCacheService;
        this.freeHashRepository = freeHashRepository;
        this.executor = executor;
    }

    public FreeHash getAvailableHash() {
        triggerAsyncRefill();
        return localCacheService.getAvailableHash();
    }

    public void triggerAsyncRefill(){
        int threshold = maxCacheCapacity * refillThresholdPercent / 100;
        int currentCacheSize = localCacheService.getCacheSize();
        if (currentCacheSize < threshold) {
            executor.execute(() -> asyncRefill(currentCacheSize));
        }
    }

    public void asyncRefill(int currentCacheSize) {
        if (!lock.compareAndSet(false, true)) {
            return;
        }

        try {
            int toRefill = maxCacheCapacity - currentCacheSize;
            localCacheService.refillCache(toRefill);
        } finally {
            lock.set(false);
        }
    }

    @Transactional
    public void warmUpCache() {
        long freeHashesInDb = freeHashRepository.count();
        if (freeHashesInDb < maxDbCapacity) {
            long refillDatabaseCount = maxDbCapacity - freeHashesInDb;
            freeHashGenerator.refillDb(refillDatabaseCount);
        }
        List<FreeHash> dbHashes = freeHashRepository.deleteAndReturnFreeHashes(maxCacheCapacity);
        localCacheService.addAll(dbHashes);
    }
}
