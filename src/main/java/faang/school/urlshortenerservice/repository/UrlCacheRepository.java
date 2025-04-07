package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final StringRedisTemplate redisCacheTemplate;

    @Value("${hash-cache.timeout}")
    private long timeout;

    public void save(String url, String hash) {
        redisCacheTemplate.opsForValue().set(hash, url, timeout, TimeUnit.DAYS);
    }
}
