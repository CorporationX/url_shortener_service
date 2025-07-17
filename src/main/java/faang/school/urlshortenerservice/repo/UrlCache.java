package faang.school.urlshortenerservice.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCache {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "url:";
    private static final long TTL_DAYS = 30;

    public void save(String hash, String originalUrl) {
        redisTemplate.opsForValue().set(
                KEY_PREFIX + hash,
                originalUrl,
                TTL_DAYS,
                TimeUnit.DAYS
        );
    }
}