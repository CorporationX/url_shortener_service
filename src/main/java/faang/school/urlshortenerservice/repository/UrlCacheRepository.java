package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.url-config.cache.ttl}")
    private long ttl;

    @Value("${app.url-config.cache.key-prefix.url-hash}")
    private String urlHashPrefix;

    public void saveUrl(String hash, String url) {
        try {
            redisTemplate.opsForValue().set(getKey(hash), url, Duration.ofDays(ttl));
        } catch (Exception e) {
            log.error("Failed to save URL to Redis: {}", e.getMessage());
        }
    }

    public String getUrl(String hash) {
        try {
            return redisTemplate.opsForValue().get(getKey(hash));
        } catch (Exception e) {
            log.error("Failed to retrieve URL from Redis: {}", e.getMessage());
            return null;
        }
    }

    public void deleteUrl(String hash) {
        try {
            redisTemplate.delete(getKey(hash));
        } catch (Exception e) {
            log.error("Failed to delete URL from Redis: {}", e.getMessage());
        }
    }

    private String getKey(String hash) {
        return urlHashPrefix + hash;
    }
}
