package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${url.redis-ttl-in-hours}")
    private long ttl;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, Duration.ofHours(ttl));
    }

    public String getUrl(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }

}
