package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, Hash> redisTemplate;
    private final HashGenerator hashGenerator;

    public Hash getHashInCache() {
        Set<String> keys = redisTemplate.keys("*");

        if (keys != null && !keys.isEmpty()) {
            String firstKey = keys.iterator().next();
             return redisTemplate.opsForValue().get(firstKey);
        } else {
            throw new IllegalStateException("No hashes in Redis cache");
        }
    }

    @CachePut(value = "hash")
    public void saveHashInCache(List<Hash> hashes) {

    }

    public boolean hashSizeValidate() {
        long currentSize = getCacheSize();
        int maxSize = 100;
        int threshold = (int) (maxSize * 0.2);

        return currentSize > threshold;
    }

    private Long getCacheSize() {
        return redisTemplate
                .getConnectionFactory()
                .getConnection()
                .serverCommands()
                .dbSize();
    }
}
