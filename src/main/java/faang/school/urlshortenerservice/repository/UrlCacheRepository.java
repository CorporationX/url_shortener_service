package faang.school.urlshortenerservice.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class UrlCacheRepository {
    private final StringRedisTemplate redisTemplate;
    private static final String HASH_KEY_PREFIX = "url:";
    private static final Duration TTL = Duration.ofHours(1);

    public UrlCacheRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String findByHash(String hash) {
        return redisTemplate.opsForValue().get(HASH_KEY_PREFIX + hash);
    }

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(
                HASH_KEY_PREFIX + hash,
                url,
                TTL
        );
    }

    public void delete(String hash) {
        redisTemplate.delete(HASH_KEY_PREFIX + hash);
    }
}
