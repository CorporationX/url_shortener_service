package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${redis_cache.ttl}")
    private int ttl;

    public void save(Url url) {
        redisTemplate.opsForValue().set(url.getHash(), url.getUrl(), Duration.ofMinutes(ttl));
    }

    public String getUrl(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
