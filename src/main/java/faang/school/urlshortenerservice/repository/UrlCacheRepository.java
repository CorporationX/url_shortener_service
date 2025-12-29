package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.RetryExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final StringRedisTemplate redisTemplate;
    private final RetryExecutor retryExecutor;

    private static final String URL_CACHE_PREFIX = "url:";
    private static final String REVERSE_CACHE_PREFIX = "url_to_hash:";
    
    @Value("${url.cache.default-ttl-hours}")
    private long defaultTtlHours;

    public void save(String hash, String url) {
        save(hash, url, defaultTtlHours, TimeUnit.HOURS);
    }

    public void save(String hash, String url, long timeout, TimeUnit unit) {
        try {
            String urlKey = URL_CACHE_PREFIX + hash;
            String reverseKey = REVERSE_CACHE_PREFIX + url;
            
            retryExecutor.execute(() -> {
                redisTemplate.opsForValue().set(urlKey, url, timeout, unit);
                redisTemplate.opsForValue().set(reverseKey, hash, timeout, unit);
                return null;
            });
            
            log.debug("Saved URL to cache with TTL: hash={}, url={}, timeout={} {}", hash, url, timeout, unit);
        } catch (Exception e) {
            log.warn("Failed to save URL to cache: hash={}, error={}", hash, e.getMessage());
        }
    }

    public String get(String hash) {
        try {
            String key = URL_CACHE_PREFIX + hash;
            String url = retryExecutor.execute(() -> 
                redisTemplate.opsForValue().get(key)
            );
            if (url != null) {
                log.debug("Retrieved URL from cache: hash={}", hash);
            }
            return url;
        } catch (Exception e) {
            log.warn("Failed to get URL from cache: hash={}, error={}", hash, e.getMessage());
            return null;
        }
    }

    public String getHashByUrl(String url) {
        try {
            String key = REVERSE_CACHE_PREFIX + url;
            String hash = retryExecutor.execute(() -> 
                redisTemplate.opsForValue().get(key)
            );
            if (hash != null) {
                log.debug("Retrieved hash from reverse cache: url={}, hash={}", url, hash);
            }
            return hash;
        } catch (Exception e) {
            log.warn("Failed to get hash from reverse cache: url={}, error={}", url, e.getMessage());
            return null;
        }
    }

    public void delete(String hash) {
        try {
            String key = URL_CACHE_PREFIX + hash;
            String url = retryExecutor.execute(() -> 
                redisTemplate.opsForValue().get(key)
            );
            
            retryExecutor.execute(() -> {
                redisTemplate.delete(key);
                if (url != null) {
                    String reverseKey = REVERSE_CACHE_PREFIX + url;
                    redisTemplate.delete(reverseKey);
                }
                return null;
            });
            
            log.debug("Deleted URL from cache: hash={}", hash);
        } catch (Exception e) {
            log.warn("Failed to delete URL from cache: hash={}, error={}", hash, e.getMessage());
        }
    }
}

