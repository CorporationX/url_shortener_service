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

    @Value("${redis-ttl-seconds}")
    private int redisTtlSeconds;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, redisTtlSeconds, TimeUnit.SECONDS);
    }

    public String getUrlByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
