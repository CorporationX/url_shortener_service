package faang.school.urlshortenerservice.repository.url;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlRedisCacheRepository implements UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.cache.redis.time-to-live}")
    private int urlTtl;

    @Value("${app.cache.redis.url.key-prefix}")
    private String keyPrefix;

    @Override
    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(
                keyPrefix + hash,
                url,
                urlTtl,
                TimeUnit.DAYS
        );
    }

    @Override
    public String get(String hash) {
        return redisTemplate.opsForValue().get(keyPrefix + hash);
    }
}
