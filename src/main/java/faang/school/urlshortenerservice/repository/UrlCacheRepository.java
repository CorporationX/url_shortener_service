package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    public void put(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, redisProperties.getTtl());
    }

    public String get(String hash) {
        Object result = redisTemplate.opsForValue().get(hash);
        return result instanceof String ? (String) result : null;
    }
}