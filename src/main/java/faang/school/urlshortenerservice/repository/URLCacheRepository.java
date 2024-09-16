package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class URLCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.cache.redis.time-to-live}")
    private long timeToLive;

    public void save(String url, String hash) {
        try {
            redisTemplate.opsForValue()
                    .set(url, hash, timeToLive, TimeUnit.SECONDS);
            redisTemplate.opsForValue()
                    .set(hash, url, timeToLive, TimeUnit.SECONDS);
            log.info("Successfully cached URL: {} with Hash: {}", url, hash);
        } catch (RedisConnectionFailureException | JedisConnectionException e) {
            log.warn("Failed to connect to Redis. Cache will be skipped for URL: {}", url, e);
        }
    }

    public Optional<String> findUrlByHash(String hash) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
        } catch (RedisConnectionFailureException | JedisConnectionException e) {
            log.warn("Failed to connect to Redis. Returning empty result for Hash: {}", hash, e);
            return Optional.empty();
        }
    }

    public Optional<String> findHashByUrl(String url) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(url));
        } catch (RedisConnectionFailureException | JedisConnectionException e) {
            log.warn("Failed to connect to Redis. Returning empty result for URL: {}", url, e);
            return Optional.empty();
        }
    }
}