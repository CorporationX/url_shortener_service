package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final StringRedisTemplate redisTemplate;

    public String getUrlByHash(String hash) {
        log.info("Fetching URL from Redis for hash: {}", hash);
        return redisTemplate.opsForValue().get(hash);
    }

    public void cacheUrl(String hash, String url, long ttlSeconds) {
        log.info("Caching URL in Redis: {} -> {} (TTL = {}s)", hash, url, ttlSeconds);
        redisTemplate.opsForValue().set(hash, url, ttlSeconds, TimeUnit.SECONDS);
    }
}