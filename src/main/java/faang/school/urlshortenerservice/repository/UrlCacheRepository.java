package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final StringRedisTemplate redisTemplate;
    private static final long CACHE_TTL = 24;

    public String getUrl(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }

    public void saveUrl(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, CACHE_TTL, TimeUnit.HOURS);
    }
}
