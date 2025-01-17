package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveWithTTL(String key, String value, Integer timeToLiveInMinutes) {
        redisTemplate.opsForValue().set(key, value, timeToLiveInMinutes, TimeUnit.MINUTES);
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
