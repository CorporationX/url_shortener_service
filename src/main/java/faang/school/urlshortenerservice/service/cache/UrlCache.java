package faang.school.urlshortenerservice.service.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class UrlCache {

    private final StringRedisTemplate redisTemplate;

    public void saveUrlMapping(String hash, String longUrl) {
        long ttlInSeconds = 365L * 24 * 60 * 60;
        redisTemplate.opsForValue().set("url:" + hash, longUrl, ttlInSeconds, TimeUnit.SECONDS);
    }

    public String getLongUrl(String hash) {
        return redisTemplate.opsForValue().get("url:" + hash);
    }
}
