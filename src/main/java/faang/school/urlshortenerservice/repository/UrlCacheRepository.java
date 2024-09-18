package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {
    @Value("${redis.cache-ttl}")
    private long cacheTtl;
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String url) {
        try {
            redisTemplate.opsForValue().set(hash, url, cacheTtl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Redis is not available, skipping cache for hash: {}", hash, e);
        }
    }

    public String getCacheValue(String hash) {
        try {
           return redisTemplate.opsForValue().get(hash);
        } catch (Exception e) {
            log.warn("Redis is not available, skipping cache lookup for hash: {}", hash, e);
            return null;
        }
    }

    public String getCacheValueByUrl(String url) {
        try {
            return redisTemplate.opsForValue().get(url);
        } catch (Exception e) {
            log.warn("Redis is not available, skipping cache lookup for URL: {}", url, e);
            return null;
        }
    }
}
