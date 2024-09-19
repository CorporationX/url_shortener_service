package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCasheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void saveUrlHash(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
        redisTemplate.expire(hash, 15, TimeUnit.MINUTES);
    }

    public String getUrlByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
