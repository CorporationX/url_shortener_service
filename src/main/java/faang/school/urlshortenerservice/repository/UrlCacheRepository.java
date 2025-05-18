package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${hash.url.cache-ttl-days:360}")
    private int ttlDays;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, ttlDays, TimeUnit.DAYS);
    }

    public String findByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
