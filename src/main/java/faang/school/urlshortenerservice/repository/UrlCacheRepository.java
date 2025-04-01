package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {
    private final RedisTemplate<String, String> hashRedisTemplate;
    @Value("${spring.data.redis.url-ttl-in-seconds:600}")
    private long timeToLive;

    @Nullable
    public String getUrl(String hash) {
        String url = null;
        try {
            url = hashRedisTemplate.opsForValue().get(hash);
            if (url != null) {
                hashRedisTemplate.expire(hash, timeToLive, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("Failed to retrieve url from Redis", e);
        }
        return url;
    }

    public void setUrl(String hash, String url) {
        try {
            hashRedisTemplate.opsForValue().set(hash, url, timeToLive, TimeUnit.MINUTES);
            log.debug("Url {} added to cache", url);
        } catch (Exception e) {
            log.error("Failed to add url to Redis", e);
        }
    }

    public void removeUrl(String hash) {
        try {
            hashRedisTemplate.delete(hash);
        } catch (Exception e) {
            log.warn("Failed to remove url from Redis", e);
        }
    }
}
