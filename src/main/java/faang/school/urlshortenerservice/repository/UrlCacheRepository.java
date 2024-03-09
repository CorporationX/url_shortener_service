package faang.school.urlshortenerservice.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public UrlCacheRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    public String findByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
