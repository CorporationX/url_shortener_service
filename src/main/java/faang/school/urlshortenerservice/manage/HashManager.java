package faang.school.urlshortenerservice.manage;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.exception.HashCacheIsEmptyException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashManager {
    private final AtomicBoolean indicateGenerationHash = new AtomicBoolean(false);
    private final HashCache hashCache;
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;


    @PostConstruct
    public void fillHash() {
        List<String> hashes = hashGenerator.generateBatch(hashCache.getFreeCapacityInCollection());
        hashCache.fillingCache(hashes);
    }

    @Retryable(retryFor = HashCacheIsEmptyException.class, backoff = @Backoff(delay = 3000))
    public String getHash() {
        if (hashCache.cacheSizeLessThanRequired()) {
            if (indicateGenerationHash.compareAndSet(false, true)) {
                hashGenerator.generateBatchAsync(hashCache.getFreeCapacityInCollection())
                        .thenAccept(hashCache::fillingCache)
                        .whenComplete((result, exception) -> indicateGenerationHash.set(false));
            }
        }

        String hash = hashCache.getHash();
        if (hash == null) {
            throw new HashCacheIsEmptyException("List with hashes is empty");
        }
        return hash;
    }

    @Transactional
    public void saveHashes(List<String> expiredHashes) {
        hashRepository.save(expiredHashes);
    }
}
