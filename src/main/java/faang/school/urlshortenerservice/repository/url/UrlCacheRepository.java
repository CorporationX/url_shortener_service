package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.config.redis.RedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisProperties redisProperties;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, redisProperties.getTtl(), TimeUnit.HOURS);
        log.info("Saved URL with hash {} to cache with TTL of {} hours", hash, redisProperties.getTtl());
    }

    public String getUrl(String hash) {
        log.info("Retrieving URL for hash {} from cache", hash);
        return redisTemplate.opsForValue().get(hash);
    }

    public void removeFromCache(String hash) {
        log.info("Removing hash {} from cache", hash);
        redisTemplate.delete(hash);
    }
}
