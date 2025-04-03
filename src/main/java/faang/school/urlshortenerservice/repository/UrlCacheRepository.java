package faang.school.urlshortenerservice.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final StringRedisTemplate redisCacheTemplate;

    @Transactional
    public void save(String url, String hash) {
        redisCacheTemplate.opsForValue().set(hash, url, 1, TimeUnit.DAYS);
    }
}
