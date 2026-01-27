package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String Url) {
        redisTemplate.opsForValue().set(hash, Url);
    }

    public String findByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
