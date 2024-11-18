package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final StringRedisTemplate redisTemplate;

    public String findUrlByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }

    public void saveUrlWithExpiry(String hash, String url, int ttlInHours) {
        redisTemplate.opsForValue().set(hash, url, Duration.ofHours(ttlInHours));
    }
}