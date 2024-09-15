package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String url, long ttlInSeconds) {
        redisTemplate.opsForValue().set(hash, url, ttlInSeconds);
    }

    public String getCacheValue(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }

    public String getCacheValueByUrl(String url) {
        return redisTemplate.opsForValue().get(url);
    }
}
