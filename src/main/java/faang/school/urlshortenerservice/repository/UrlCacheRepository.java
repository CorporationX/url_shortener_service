package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long CACHE_TTL = 7;

    public void save(String hash, String originalUrl) {
        redisTemplate.opsForValue().set(hash, originalUrl, CACHE_TTL, TimeUnit.DAYS);
    }

    public String findByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }

    public void printValue(String hash) {
        String value = redisTemplate.opsForValue().get(hash);
        System.out.println("Value for hash " + hash + ": " + value);
    }
}
