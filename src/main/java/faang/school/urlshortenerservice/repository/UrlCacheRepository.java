package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UrlCacheRepository {
    private final StringRedisTemplate stringRedisTemplate;

    public UrlCacheRepository(StringRedisTemplate redisTemplate) {
        this.stringRedisTemplate = redisTemplate;
    }

    public void save(String hash, String longUrl) {
        stringRedisTemplate.opsForValue().set(hash, longUrl);
    }

    public String get(String hash) {
        return stringRedisTemplate.opsForValue().get(hash);
    }
}
