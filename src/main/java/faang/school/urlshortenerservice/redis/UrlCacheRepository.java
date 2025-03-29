package faang.school.urlshortenerservice.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final StringRedisTemplate redisTemplate;

    @Value("${redis.ttl-hours:24}")
    private long ttlHours;

    public void save(String hash, String url) {
        redisTemplate.opsForValue()
                .set(hash, url, ttlHours, TimeUnit.HOURS);
    }

    public Optional<String> findByHash(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        return Optional.ofNullable(url);
    }
}
