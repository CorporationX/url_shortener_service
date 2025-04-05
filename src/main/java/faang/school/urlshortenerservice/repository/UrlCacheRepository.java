package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final StringRedisTemplate redisTemplate;

    public void save(String url, String hash) {
        redisTemplate.opsForValue().set(hash, url, 1, TimeUnit.DAYS);
    }
}
