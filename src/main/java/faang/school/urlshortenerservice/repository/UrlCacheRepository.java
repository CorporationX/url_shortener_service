package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private static final String KEY_PREFIX = "url:";

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String keyHash, String url, long ttlInSeconds) {
        redisTemplate.opsForValue().set(KEY_PREFIX + keyHash, url, ttlInSeconds, TimeUnit.SECONDS);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + key);
    }
}