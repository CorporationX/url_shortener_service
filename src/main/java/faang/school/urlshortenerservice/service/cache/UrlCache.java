package faang.school.urlshortenerservice.service.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class UrlCache {

    private final StringRedisTemplate redisTemplate;

    @Value("${hash.cache.ttl-in-seconds:31536000}")
    private long ttlInSeconds;

    public void saveUrlMapping(String hash, String longUrl) {
        redisTemplate.opsForValue().set("url:" + hash, longUrl, ttlInSeconds, TimeUnit.SECONDS);
    }

    public String getLongUrl(String hash) {
        return redisTemplate.opsForValue().get("url:" + hash);
    }
}
