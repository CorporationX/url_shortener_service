package faang.school.urlshortenerservice.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlCache {

    private final StringRedisTemplate redisTemplate;

    @Value("${hash.cache.ttl-in-seconds:31536000}")
    private long ttlInSeconds;

    public void saveUrlMapping(String hash, String longUrl) {
        log.info("Saving URL mapping to Redis: {} -> {}", hash, longUrl);
        redisTemplate.opsForValue().set("url:" + hash, longUrl, ttlInSeconds, TimeUnit.SECONDS);
    }

    public String getLongUrl(String hash) {
        String url = redisTemplate.opsForValue().get("url:" + hash);
        log.debug("Retrieved URL from Redis for hash {}: {}", hash, url != null ? url : "Not found");
        return url;
    }
}