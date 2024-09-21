package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.dto.UrlDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.cache.redis.time-to-live}")
    private int ttl;

    public void save(UrlDto urlDto) {
        try {
            redisTemplate.opsForValue().set(urlDto.getUrl(), urlDto.getHash(), ttl, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(urlDto.getHash(), urlDto.getUrl(), ttl, TimeUnit.SECONDS);
            log.info("Put into cache: {}", urlDto);
        } catch (RedisConnectionFailureException | JedisConnectionException e) {
            log.error("Failed to connect Redis: {}", e.getMessage());
        }
    }

    public String getHashByUrl(String url) {
        try {
            return redisTemplate.opsForValue().get(url);
        } catch (RedisConnectionFailureException | JedisConnectionException e) {
            log.error("Failed to connect to Redis: {}", e.getMessage());
            return null;
        }
    }


    public String getUrlByHash(String hash) {
        try {
            return redisTemplate.opsForValue().get(hash);
        } catch (RedisConnectionFailureException | JedisConnectionException e) {
            log.error("Failed to connect to Redis: {}", e.getMessage());
            return null;
        }
    }
}