package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.mapper.HashMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
@Service
@RequiredArgsConstructor
public class HashCacheService {

    private final HashService hashService;
    private final HashMapper hashMapper;

    @Qualifier("hashRedisTemplate")
    private final RedisTemplate<String, String> redis;

    @Value("${service.hash-cache-service.capacity}")
    private long capacity;

    @Value("${service.hash-cache-service.percent-of-filled-cache-to-start-generation}")
    private int percentOfFilledCacheToStartGeneration;

    private final AtomicBoolean fillingCache = new AtomicBoolean(false);
    private static final String HASH_QUEUE_KEY = "hash-queue";

    @PostConstruct
    public void init() {
        log.info("Initializing HashCacheService with capacity: {}", capacity);
        hashService.getHashes(capacity)
                .thenAccept(this::addHashesToRedis)
                .exceptionally(ex -> {
                    log.error("Error with loading to Redis", ex);
                    return null;
                });
    }

    public String getHash() {
        log.info("Getting hash from cache");
        if (countPercentOfFilledCache() < percentOfFilledCacheToStartGeneration) {
            log.info("Cache is less than {}% filled, starting to fill cache",
                    percentOfFilledCacheToStartGeneration);

            if (fillingCache.compareAndSet(false, true)) {
                log.info("Cache filling started");
                hashService.getHashes(capacity)
                        .thenAccept(this :: addHashesToRedis)
                        .thenRun(() -> fillingCache.set(false));
            }
        }
        return  redis.opsForList().leftPop(HASH_QUEUE_KEY);
    }

    private void addHashesToRedis(List<Hash> hashes) {
        if (hashes != null && !hashes.isEmpty()) {
            redis.opsForList().rightPushAll(HASH_QUEUE_KEY, hashMapper.toListStringFromHash(hashes));
        }
    }

    private long countPercentOfFilledCache() {
        long currentSize = Optional.ofNullable(redis.opsForList().size(HASH_QUEUE_KEY)).orElse(0L);
        if (capacity == 0) {
            return 0;
        }

        return  currentSize * 100 / capacity;
    }
}
