package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisProperties redisProperties;
    private final RedisTemplate<String, String> redisTemplate;

    public void put(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl, redisProperties.getTtlDays(), TimeUnit.DAYS);
    }

    public String get(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
