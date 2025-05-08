package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TTL_DURATION = Duration.ofDays(7);

    public String get(String shortHash) {
        String originalUrl = redisTemplate.opsForValue().get(shortHash);
        if (originalUrl != null) {
            redisTemplate.expire(shortHash, TTL_DURATION);
        }
        return originalUrl;
    }
}
