package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, Duration.ofDays(14));
    }

    public String getUrl(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        if(url != null) {
            redisTemplate.expire(hash, Duration.ofDays(14));
        }
        return url;
    }
}
