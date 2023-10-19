package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisUrlTemplate;
    private static final String CACHE_KEY_PREFIX = "url_cache:";

    @Retryable(
            value = {OptimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000))
    public void saveToCache(String hash, String originalURL) {
        try {
            String cacheKey = CACHE_KEY_PREFIX + hash;
            redisUrlTemplate.opsForValue().set(cacheKey, originalURL);
            redisUrlTemplate.expire(cacheKey, 48, TimeUnit.HOURS);
        } catch (OptimisticLockingFailureException ex) {
            log.error("Error saving URL in cache", ex);
        }
    }

    public String getFromCache(String hash) {
        try {
            String cacheKey = CACHE_KEY_PREFIX + hash;
            return redisUrlTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.error("Error retrieving URL from cache for hash {}: {}", hash, e.getMessage(), e);
            return null;
        }
    }
}