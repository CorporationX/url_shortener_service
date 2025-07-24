package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final StringRedisTemplate redisTemplate;

    public void cacheHash(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getUrl(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
