package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${spring.redis.ttl:24}")
    private long ttl;

    public void put(String key, String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofHours(ttl));
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
