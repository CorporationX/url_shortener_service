package faang.school.urlshortenerservice.repository;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> hashRedisTemplate;

    @Value("${url.short-url-ttl-in-seconds}")
    private long timeoutInSeconds;

    @Timed(value = "url.cache.getUrl", description = "Time taken to get URL from cache")
    public String getUrl(String hash) {
        try {
            String url = hashRedisTemplate.opsForValue().get(hash);
            if (url != null) {
                hashRedisTemplate.expire(hash, timeoutInSeconds, TimeUnit.SECONDS);
                log.debug("Retrieved URL from Redis for hash: {}", hash);
            }
            return url;
        } catch (Exception e) {
            log.error("Unexpected error retrieving URL for hash: {}", hash, e);
            return null;
        }
    }

    @Timed(value = "url.cache.setUrl", description = "Time taken to set URL in cache")
    public void setUrl(String hash, String url) {
        try {
            hashRedisTemplate.opsForValue().set(hash, url, timeoutInSeconds, TimeUnit.SECONDS);
            log.debug("Cached URL for hash: {}", hash);
        } catch (Exception e) {
            log.error("Unexpected error while caching URL for hash: {}", hash, e);
        }
    }

    @Timed(value = "url.cache.removeUrl", description = "Time taken to remove URL from cache")
    public void removeUrl(String hash) {
        try {
            hashRedisTemplate.delete(hash);
            log.debug("Removed URL from Redis for hash: {}", hash);
        } catch (Exception e) {
            log.error("Unexpected error while removing hash: {}", hash, e);
        }
    }
}