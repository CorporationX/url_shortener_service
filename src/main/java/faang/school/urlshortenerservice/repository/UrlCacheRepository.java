package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.ttl-hours}")
    private long ttlHours;

    public void save(String hash, String url) {
        try {
            redisTemplate.opsForValue().set(hash, url, ttlHours, TimeUnit.HOURS);
            log.info("Saved data in Redis: hash='{}', URL='{}'", hash, url);
        } catch (Exception e) {
            log.error("Error occurred when trying to save data in Redis: {}", e.getMessage());
        }
    }

    public String get(String hash) {
        try {
            String url = redisTemplate.opsForValue().get(hash);
            log.debug("Got data from Redis: hash='{}', URL='{}'", hash, url);
            updateTtl(hash);
            return url;
        } catch (Exception e) {
            log.error("Error occurred when trying to get data from Redis: {}", e.getMessage());
            return null;
        }
    }

    private void updateTtl(String hash) {
        try {
            redisTemplate.expire(hash, ttlHours, TimeUnit.HOURS);
            log.debug("TTL updated in Redis: hash='{}', URL='{}'", hash, ttlHours);
        } catch (Exception e) {
            log.error("Error occurred when trying to update TTL in Redis: {}", e.getMessage());
        }
    }
}