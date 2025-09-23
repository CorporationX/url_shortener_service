package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * UrlCacheRepository — репозиторий для работы с кэшом Redis.
 *
 * @author bozya
 * @since 18.09.2025
 */
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final StringRedisTemplate redisTemplate;

    public String findUrlByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }

    public void save(String hash, String originalUrl) {
        redisTemplate.opsForValue().set(hash, originalUrl);
    }

    public boolean exists(String hash) {
        return redisTemplate.hasKey(hash);
    }

    public void delete(String hash) {
        redisTemplate.delete(hash);
    }
}