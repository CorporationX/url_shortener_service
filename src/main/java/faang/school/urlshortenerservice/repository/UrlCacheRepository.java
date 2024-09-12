package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${cache.url-cache-parameters.days-in-cache}")
    private long daysInCache;
    public void put(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, Duration.ofDays(daysInCache));
    }

    public Optional<String> get(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }
}
