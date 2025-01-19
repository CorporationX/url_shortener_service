package faang.school.urlshortenerservice.redis.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UrlCacheRepository {
    private RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl);
    }
}
