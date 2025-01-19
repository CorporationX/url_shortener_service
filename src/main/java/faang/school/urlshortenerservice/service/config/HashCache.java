package faang.school.urlshortenerservice.service.config;

import com.github.benmanes.caffeine.cache.Cache;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
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
    private final CacheManager caffeineCacheManager;
    private final RedisCacheManager cacheManager;
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

    public String getHash() {
        Cache<String, String> cache =
                (Cache<String, String>)(cacheManager.getCache("hashCache")).getNativeCache();
        Map<String, String> cacheMap = cache.asMap();
        if (cacheMap.size() > maximumCapacity * cacheMinPercentage / 100) {
            Map.Entry<String, String> entry = cacheMap.entrySet().iterator().next();
            String key = entry.getKey();
            String value = entry.getValue();
            cache.invalidate(key);
            return value;
        } else {
            getHashesFromDBAsyncly();
        }
        return null;
    }

    private void storeHashesToCache() {
        hashGenerator.generateBatch();
        Cache<String, String> cache =
                (Cache<String, String>)(cacheManager.getCache("hashCache")).getNativeCache();
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
                    throw new RuntimeException(e);
                } finally {
                    isGettingHashesFromDB.set(false);
                    lock.unlock();
                    log.info("Unlock the thread after getting the hashes from DB");
                }
            });
        }
    }
}
