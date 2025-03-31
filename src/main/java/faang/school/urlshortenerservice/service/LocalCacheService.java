package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class LocalCacheService {
    private static final Queue<FreeHash> FREE_HASHES_CACHE = new ConcurrentLinkedQueue<>();

    private final FreeHashGenerator freeHashGenerator;
    private final FreeHashRepository freeHashRepository;
    private final Executor executor;

    @Value("${shortener.hash-pool.max-postgres-capacity}")
    private long maxDbCapacity;

    public LocalCacheService(FreeHashGenerator freeHashGenerator,
                             FreeHashRepository freeHashRepository,
                             @Qualifier("hashServiceExecutor") Executor executor) {
        this.freeHashGenerator = freeHashGenerator;
        this.freeHashRepository = freeHashRepository;
        this.executor = executor;
    }

    public FreeHash getAvailableHash() {
        return FREE_HASHES_CACHE.poll();
    }

    public int getCacheSize() {
        return FREE_HASHES_CACHE.size();
    }

    public void addAll(List<FreeHash> hashes) {
        FREE_HASHES_CACHE.addAll(hashes);
    }

    @Transactional
    public void refillCache(int toRefill) {
        List<FreeHash> dbHashes = freeHashRepository.deleteAndReturnFreeHashes(toRefill);
        FREE_HASHES_CACHE.addAll(dbHashes);
        log.info("Added {} hashes from DB to cache", dbHashes.size());

        long freeHashesInDb = freeHashRepository.count();

        if (freeHashesInDb < maxDbCapacity) {
            long refillDatabaseCount = maxDbCapacity - freeHashesInDb;
            executor.execute(() -> freeHashGenerator.refillDb(refillDatabaseCount));
        }
    }
}
