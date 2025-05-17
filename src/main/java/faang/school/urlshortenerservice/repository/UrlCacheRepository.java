package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long CACHE_TTL = 7;

    public void save(String hash, String originalUrl) {
        redisTemplate.opsForValue().set("hash:" + hash, originalUrl, CACHE_TTL, TimeUnit.DAYS);
        redisTemplate.opsForValue().set("url:" + originalUrl, hash, CACHE_TTL, TimeUnit.DAYS);
    }

    public String findByHash(String hash) {
        return redisTemplate.opsForValue().get("hash:" + hash);
    }

    public String findHashByUrl(String originalUrl) {
        return redisTemplate.opsForValue().get("url:" + originalUrl);
    }

    public void printValue(String hash) {
        String value = redisTemplate.opsForValue().get(hash);
        System.out.println("Value for hash " + hash + ": " + value);
    }
}
