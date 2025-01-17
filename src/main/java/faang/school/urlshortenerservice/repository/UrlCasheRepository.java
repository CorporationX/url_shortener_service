package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class UrlCasheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TTL_Duration = Duration.ofDays(1);

    public String getUrl(String hash) {
        String originalUrl = redisTemplate.opsForValue().get(hash);
        if (originalUrl != null) {
            redisTemplate.expire(hash, TTL_Duration);
        }
        return originalUrl;
    }

    public void saveUrl(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, TTL_Duration);
    }
}
