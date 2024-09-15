package faang.school.urlshortenerservice.repository.url;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    @Value("${url.cache.ttl}")
    private Duration timeout;
    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, timeout);
    }
}
