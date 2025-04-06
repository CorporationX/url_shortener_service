package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    @Value("${data.cache.timeout}")
    private int timeout;

    private final StringRedisTemplate redisTemplate;

    public void save(String url, String hash) {
        redisTemplate.opsForValue().set(hash, url, timeout, TimeUnit.DAYS);
    }
}
