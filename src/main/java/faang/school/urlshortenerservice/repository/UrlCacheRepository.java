package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.redis.properties.RedisCacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisCacheProperties redisCacheProperties;

    public void saveRedisCache(String hash, String url) {
        redisTemplate.opsForValue().set(
                hash,
                url,
                redisCacheProperties.timeToLive()
        );
    }

    public String getRedisCache(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
