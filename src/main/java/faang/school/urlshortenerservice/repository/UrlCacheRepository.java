package faang.school.urlshortenerservice.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@AllArgsConstructor
public class UrlCacheRepository {
    private RedisTemplate<String, Object> redisTemplate;

    public void saveUrlForTime(String hash, String url, int time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(hash, url, time, timeUnit);
    }
}
