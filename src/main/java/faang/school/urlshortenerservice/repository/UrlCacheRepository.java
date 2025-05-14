package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
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

    public String getUrl(String hash) {
        try {
            String url = hashRedisTemplate.opsForValue().get(hash);
            if (url != null) {
                hashRedisTemplate.expire(hash, timeoutInSeconds, TimeUnit.SECONDS);
                log.debug("Retrieved URL from Redis for hash: {}", hash);
            }
            return url;
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection error for hash: {}", hash, e);
            return null;
        } catch (Exception e) {
            log.error("Unexpected error retrieving URL for hash: {}", hash, e);
            return null;
        }
    }

    public void setUrl(String hash, String url) {
        try {
            hashRedisTemplate.opsForValue().set(hash, url, timeoutInSeconds, TimeUnit.SECONDS);
            log.debug("Cached URL for hash: {}", hash);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection error while caching URL for hash: {}", hash, e);
        } catch (Exception e) {
            log.error("Unexpected error while caching URL for hash: {}", hash, e);
        }
    }

    public void removeUrl(String hash) {
        try {
            hashRedisTemplate.delete(hash);
            log.debug("Removed URL from Redis for hash: {}", hash);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection error while removing hash: {}", hash, e);
        } catch (Exception e) {
            log.error("Unexpected error while removing hash: {}", hash, e);
        }
    }
}