package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class UrlCasheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${cache.ttl-duration}")
    private long ttlDuration;

    public String getUrl(String hash) {
        String originalUrl = redisTemplate.opsForValue().get(hash);
        if (originalUrl != null) {
            redisTemplate.opsForValue().get(hash);
        }
        return originalUrl;
    }

    public void saveUrl(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, ttlDuration);
    }
}
