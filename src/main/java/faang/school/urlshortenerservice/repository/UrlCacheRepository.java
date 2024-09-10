package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.hash_cache.ttl_in_seconds}")
    private int ttlInSeconds;

    public void saveUrl(Url url) {
        redisTemplate.opsForValue().set(url.getHash(), url);
        redisTemplate.expire(url.getHash(), Duration.ofSeconds(ttlInSeconds));
    }

    public Url getUrl(String hash) {
        return (Url) redisTemplate.opsForValue().get(hash);
    }
}
