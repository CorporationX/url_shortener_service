package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void putToCache(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    public String getFromCache(String hash) {
        return (String) redisTemplate.opsForValue().get(hash);
    }
}
