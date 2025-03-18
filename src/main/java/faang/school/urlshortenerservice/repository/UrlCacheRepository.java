package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.cache.url-expiration-in-minutes}")
    private int expiration;
    @Value("${spring.cache.url-prefix}")
    private String prefix;

    public void cacheCouple(String hash, String endPoint) {
        redisTemplate.opsForValue().set(prefix + hash, endPoint, expiration, TimeUnit.MINUTES);
    }

    public String getEndPointByHash(String hash) {
        Object value = redisTemplate.opsForValue().get(prefix + hash);
        if (value == null) {
            return null;
        } else {
            return (String) value;
        }
    }
}
