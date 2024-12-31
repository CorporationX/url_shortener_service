package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.ttl-days}")
    private long ttl;

    public void put(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl, ttl, TimeUnit.DAYS);
    }

    public String get(String hash) {
        return redisTemplate.opsForValue().getAndExpire(hash, ttl, TimeUnit.DAYS);
    }
}