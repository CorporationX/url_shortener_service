package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final StringRedisTemplate stringRedisTemplate;

    public void save(String hash, String url) {
        stringRedisTemplate.opsForValue().set(hash, url,1, TimeUnit.DAYS);
    }

    public String get(String hash) {
        return stringRedisTemplate.opsForValue().get(hash);
    }
}
