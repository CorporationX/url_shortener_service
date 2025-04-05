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

    @Value("${spring.redis.records-list}")
    private String recordListKey;

    @Value("${spring.redis.max-records}")
    private int maxRecords;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, Duration.ofHours(ttl));

        redisTemplate.opsForList().leftPush(recordListKey, hash);
        Long count = redisTemplate.opsForList().size(recordListKey);

        if (count != null && count > maxRecords) {
            String oldestKey = redisTemplate.opsForList().rightPop(recordListKey);
            if (oldestKey != null) {
                redisTemplate.delete(oldestKey);
            }
        }
    }

    public String getUrl(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
