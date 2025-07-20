package faang.school.urlshortenerservice.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlCacheImpl implements UrlCache {
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${spring.data.redis.hashes.ttl-minutes}")
    private int cacheTtlMinutes;
    @Value("${spring.data.redis.hashes.cache-name}")
    private String cacheName;

    @Override
    public void addToCache(String hash, String url) {
        log.info("Adding to cache(): url - {}, hash - {}", url, hash);
        try {
            redisTemplate.opsForValue().set(String.format("%s::%s", cacheName, hash), url,
                    Duration.ofMinutes(cacheTtlMinutes));
        } catch (Exception e) {
            log.error("Error while saving value to cache, hash: {}, url: {}", hash, url, e);
        }
    }
}