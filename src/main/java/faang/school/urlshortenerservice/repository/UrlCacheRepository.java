package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public void put(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    public String get(String hash) {
        Object result = redisTemplate.opsForValue().get(hash);
        return result instanceof String ? (String) result : null;
    }
}