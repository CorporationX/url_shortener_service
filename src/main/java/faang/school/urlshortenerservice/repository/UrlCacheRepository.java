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
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.hash_cache.ttl_in_seconds}")
    private int ttlInSeconds;

    public void saveUrl(Url url) {
        redisTemplate.opsForValue().set(url.getHash(), url.getUrl());
        redisTemplate.expire(url.getHash(), Duration.ofSeconds(ttlInSeconds));
    }

    public String getUrl(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
