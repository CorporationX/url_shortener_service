package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TTl_DURATION = Duration.ofDays(7);

    public void save(String shortHash, String originalUrl){
        redisTemplate.opsForValue().set(shortHash, originalUrl, TTl_DURATION);
    }

    public String get(String shortHash){
        String originalUrl = redisTemplate.opsForValue().get(shortHash);
        if (originalUrl != null){
            redisTemplate.expire(shortHash, TTl_DURATION);
        }
        return originalUrl;
    }
    public void deleteAll(List<String> hashes) {
        redisTemplate.delete(hashes);
    }
}
