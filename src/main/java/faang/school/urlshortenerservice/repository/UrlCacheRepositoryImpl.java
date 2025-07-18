package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepositoryImpl implements UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String hash, String url, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(hash, url, ttl);
            log.info("Successfully cached hash '{}' with TTL {} of seconds.", hash, ttl.getSeconds());
        } catch (Exception e) {
            log.error("Error saving hash {} to Redis. The main opperation will still succeed.", hash, e);
        }
    }
}
