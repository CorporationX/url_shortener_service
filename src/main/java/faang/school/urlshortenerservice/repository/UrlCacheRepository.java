package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private static final String PREFIX = "url:";
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String url) {
        String key = PREFIX + hash;
        redisTemplate.opsForValue().set(key, url, 3, TimeUnit.DAYS);
    }

}