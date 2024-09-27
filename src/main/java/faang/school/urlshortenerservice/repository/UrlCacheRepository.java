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
    @Value("${spring.redis.ttl_hours}")
    private long ttl;

    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl, Duration.ofHours(ttl));
    }

    public String findLongUrlByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
