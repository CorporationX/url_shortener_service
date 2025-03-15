package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;
    private final HashGenerator hashGenerator;

    @Value("${hash.cache.max-size}")
    private int maxSize;

    @Value("${hash.cache.fill-threshold}")
    private double fillThreshold;

    @Value("${hash.cache.key}")
    private String key;

    @Value("${hash.cache.lock}")
    private String lock;

    @PostConstruct
    public void initCash() {
        fillCache();
    }

    public String getHash() {
        Long cacheSize = redisTemplate.opsForList().size(key);
        if (cacheSize != null && cacheSize > maxSize * fillThreshold) {
            return redisTemplate.opsForList().rightPop(key);
        }

        fillCacheAsync();
        return redisTemplate.opsForList().rightPop(key);
    }

    @Async("hashCacheExecutor")
    private void fillCacheAsync() {
        RLock block = redissonClient.getLock(lock);
        if (block.tryLock()) {
            hashGenerator.getHashesAsync()
                    .thenAccept(hashes -> redisTemplate.opsForList().leftPushAll(key, hashes))
                    .thenRun(block::unlock);
        }
    }

    private void fillCache() {
        List<String> hashes = hashGenerator.getHashes();
        redisTemplate.opsForList().leftPushAll(key, hashes);
    }
}
