package faang.school.urlshortenerservice.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UrlCacheRepository {
    private final StringRedisTemplate redisTemplate;

    public UrlCacheRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl);
    }

    public String get(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
