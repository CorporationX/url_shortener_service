package faang.school.urlshortenerservice.repository.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UrlCacheRepository {
    private RedisTemplate<String, String> redisTemplate;

    public Optional<String> getUrlByHash(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue()
                .get(hash));
    }

    public void saveUrlByHash(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
    }
}
