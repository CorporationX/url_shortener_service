package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.ttl-hours}")
    private long ttlHours;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, ttlHours, TimeUnit.HOURS);
        log.debug("Saved data in Redis: hash='{}', URL='{}'", hash, url);
    }

    public Optional<String> get(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        log.debug("Got data from Redis: hash='{}', URL='{}'", hash, url);
        updateTtl(hash);

        return Optional.ofNullable(url);
    }

    private void updateTtl(String hash) {
        redisTemplate.expire(hash, ttlHours, TimeUnit.HOURS);
        log.debug("TTL updated in Redis: hash='{}', URL='{}'", hash, ttlHours);

    }
}