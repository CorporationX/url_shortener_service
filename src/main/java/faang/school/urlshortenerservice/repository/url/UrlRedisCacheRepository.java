package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.exception.CacheOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UrlRedisCacheRepository implements UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.cache.redis.time-to-live}")
    private int urlTtl;

    @Value("${app.cache.redis.url.key-prefix}")
    private String keyPrefix;

    @Override
    public void save(String hash, String url) {
        try {
            redisTemplate.opsForValue().set(
                    keyPrefix + hash,
                    url,
                    urlTtl,
                    TimeUnit.DAYS
            );
            log.info("Saved URL in Redis with hash: {}",
                    hash);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed while saving hash: {}", hash, e);
            throw new CacheOperationException("Failed to save URL in Redis", e);
        } catch (Exception e) {
            log.error("Unexpected Redis error for hash: {}", hash, e);
            throw new CacheOperationException("Unexpected Redis error", e);
        }
    }

    @Override
    public String get(String hash) {
        return redisTemplate.opsForValue().get(keyPrefix + hash);
    }
}
