package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final StringRedisTemplate redisTemplate;

    public String getUrlByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }

    public void save(String url, String hash) {
        redisTemplate.opsForValue().set(hash, url);
    }
}
