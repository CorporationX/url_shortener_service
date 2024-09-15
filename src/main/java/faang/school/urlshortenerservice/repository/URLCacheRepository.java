package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class URLCacheRepository {

    private static final String CACHE_PREFIX = "url:";

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.cache.redis.time-to-live}")
    private long timeToLive;

    public void save(String hash, String url) {
        redisTemplate.opsForValue()
                .set(CACHE_PREFIX + hash, url, timeToLive, TimeUnit.SECONDS);
    }

    public String getUrl(String hash) {
        return redisTemplate.opsForValue().get(CACHE_PREFIX + hash);
    }
}
