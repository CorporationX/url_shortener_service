package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RedisShortenerRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${shortener.redis-short-url-prefix}")
    private String shortUrlPrefix;

    @Value("${shortener.max-url-redis-ttl-seconds}")
    private int maxRedisTtlSeconds;

    public void saveShortUrl(String hash, String longUrl, long ttlSeconds) {
        long ttl = Math.min(ttlSeconds, maxRedisTtlSeconds);

        redisTemplate.opsForHash().put(shortUrlPrefix, hash, longUrl);
        redisTemplate.expire(shortUrlPrefix, ttl, TimeUnit.SECONDS);
    }

    public String getLongUrl(String hash) {
        return (String) redisTemplate.opsForHash().get(shortUrlPrefix, hash);
    }
}
