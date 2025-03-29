package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void saveUrl(String hash, String originalUrl) {
        redisTemplate.opsForValue().set(hash, originalUrl);
    }

    public String getUrl(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }

    public boolean containsUrl(String hash) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(hash));
    }

    public void deleteByHash(String hash) {
        redisTemplate.delete(hash);
    }
}
