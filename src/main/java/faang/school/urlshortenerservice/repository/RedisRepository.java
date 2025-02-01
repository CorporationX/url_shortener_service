package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${hash.redis.redis-ttl-minutes}")
    private long redisTtlMinutes;

    @Value("${hash.redis.prefixes.url-prefix}")
    private String redisUrlPrefix;


    public void setUrl(String hash, String url) {
        log.info("Save to Redis:{}, {}, {}", redisUrlPrefix, hash, url);
        try {
            redisTemplate.opsForValue().set(redisUrlPrefix+hash, url, redisTtlMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Redis is not available, prefix:{}, hash: {} url: {} ",redisUrlPrefix,  hash, url, e);
        }
    }

    public String getUrl(String hash) {
        log.info("Get from Redis: {}, {}",redisUrlPrefix,  hash);
        try {
            return redisTemplate.opsForValue().get(redisUrlPrefix+hash);
        } catch (Exception e) {
            log.info("Redis is not available, hash:{} {}", redisUrlPrefix, hash, e);
            return null;
        }
    }
}