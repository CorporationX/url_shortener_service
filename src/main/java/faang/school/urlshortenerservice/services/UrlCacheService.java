package faang.school.urlshortenerservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Slf4j
@Service
@RequiredArgsConstructor
public class UrlCacheService {
    
    private final StringRedisTemplate redisTemplate;
    
    @Value("${url-shortener.cache.ttl-seconds:3600}")
    private long cacheTtlSeconds;
    
    private static final String URL_KEY_PREFIX = "url:";
    private static final String CLICK_COUNT_KEY_PREFIX = "clicks:";

    public void cacheUrl(String hash, String originalUrl) {
        String key = URL_KEY_PREFIX + hash;
        redisTemplate.opsForValue().set(key, originalUrl, Duration.ofSeconds(cacheTtlSeconds));
        log.debug("Cached URL: hash={}, url={}", hash, originalUrl);
    }

    public String getCachedUrl(String hash) {
        String key = URL_KEY_PREFIX + hash;
        String url = redisTemplate.opsForValue().get(key);
        if (url != null) {
            log.debug("Cache hit: hash={}", hash);
        }
        return url;
    }

    public void incrementClickCount(String hash) {
        String key = CLICK_COUNT_KEY_PREFIX + hash;
        redisTemplate.opsForValue().increment(key);
    }
}
