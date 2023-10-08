package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String originalUrl) {
        redisTemplate.opsForValue().set(hash, originalUrl);
    }

    public String getByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
