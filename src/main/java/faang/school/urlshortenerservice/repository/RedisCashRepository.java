package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisCashRepository {
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
