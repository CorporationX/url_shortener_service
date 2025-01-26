package faang.school.urlshortenerservice.service.config;

import com.github.benmanes.caffeine.cache.Cache;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.NoHashValueException;
import faang.school.urlshortenerservice.repository.jpa.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Data
@Slf4j
public class HashCache {
    private final ExecutorService executorService;
    private final CacheManager cacheManager;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicBoolean isGettingHashesFromDB = new AtomicBoolean(false);

    @Value("${spring.properties.cache-pros.maximum-capacity}")
    private int maximumCapacity;

    @Value("${spring.properties.cache-pros.initial-capacity}")
    private int batchSize;

    @Value("${spring.properties.cache-pros.cache-min-percentage}")
    private int cacheMinPercentage;

    @PostConstruct
    private void getHashBatch() {
        storeHashesToCache();
    }

    @Retryable(
            value = { NoHashValueException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 500, multiplier = 1.5)
    )
    public Hash getHash() {
        Cache<String, String> cache =
                (Cache<String, String>)(cacheManager.getCache("hashes")).getNativeCache();
        Map<String, String> cacheMap = cache.asMap();
        if (cacheMap.size() < maximumCapacity * cacheMinPercentage / 100) {
            getHashesFromDBAsyncly();
            throw new NoHashValueException("No hash available in Cache");
        }

        Map.Entry<String, String> entry = cacheMap.entrySet().iterator().next();
        String key = entry.getKey();
        String value = entry.getValue();
        cache.invalidate(key);
        return new Hash(value);
    }

    @Recover
    public String recover(NoHashValueException ex) {
        log.error("Unable to fetch a hash after multiple attempts", ex);
        throw new IllegalStateException(ex.getMessage());
    }

    private void storeHashesToCache() {
        Cache<String, String> cache =
                (Cache<String, String>)(cacheManager.getCache("hashes")).getNativeCache();
        Map<String, String> hashStorage = hashRepository.getHashBatch(batchSize).stream()
                .collect(Collectors.toMap(Hash::getHash, Hash::getHash));
        cache.putAll(hashStorage);
    }

    private void getHashesFromDBAsyncly() {
        if (isGettingHashesFromDB.compareAndSet(false, true)) {
            executorService.submit(() -> {
                lock.lock();
                log.info("Lock the thread to get hashes from DB");
                try {
                    hashGenerator.generateBatch();
                    storeHashesToCache();
                } catch (Exception e) {
                    log.error("Received an error while generating or getting hashes from DB", e);
                    throw new RuntimeException(e.getMessage());
                } finally {
                    isGettingHashesFromDB.set(false);
                    lock.unlock();
                    log.info("Unlock the thread after getting the hashes from DB");
                }
            });
        }
    }
}
