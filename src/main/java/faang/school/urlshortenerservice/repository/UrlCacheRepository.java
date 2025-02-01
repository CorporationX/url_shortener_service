package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.concurrent.TimeUnit;
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long CACHE_EXPIRATION = 1;

    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl, CACHE_EXPIRATION, TimeUnit.DAYS);
    }
    public String find(String hash) {
        Object cachedUrl = redisTemplate.opsForValue().get(hash);
        return cachedUrl != null ? cachedUrl.toString() : null;
    }

    public void delete(String hash) {
        redisTemplate.delete(hash);
    }
}