package faang.school.urlshortenerservice.manager;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.exception.EmptyHashCacheException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
public class HashManager {

    private final HashCache hashCache;
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final UrlRepositoryManager urlRepositoryManager;
    private final AtomicBoolean isLock = new AtomicBoolean();

    @PostConstruct
    public void fillHash() {
        List<String> hashes = hashGenerator.generateHashBatch(hashCache.getInitialCapacity());
        hashCache.fillCache(hashes);
    }

    @Retryable(retryFor = EmptyHashCacheException.class, backoff = @Backoff(delay = 2000))
    public String getHash() {
        if (hashCache.isCacheSizeLessMinimumRequired()) {
            if (isLock.compareAndSet(false, true)) {
                hashGenerator.generateHashBatchAsync(hashCache.getInitialCapacity())
                        .thenAccept(hashCache::fillCache)
                        .whenComplete((result, exception) -> isLock.set(false));
            }
        }
        String hash = hashCache.getHash();
        if (hash == null) {
            throw new EmptyHashCacheException("List with hashes is empty");
        }
        return hash;
    }

    @Transactional
    public void clearExpiredHashes(LocalDate expirationDate) {
        List<String> expiredHashes = urlRepositoryManager.getExpiredHashesAndDelete(expirationDate);
        hashRepository.saveHashes(expiredHashes);
    }
}
