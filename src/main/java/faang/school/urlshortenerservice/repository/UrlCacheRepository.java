package faang.school.urlshortenerservice.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class UrlCacheRepository {

    private final StringRedisTemplate redisTemplate;

    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl);
    }

    public Optional<String> findByHash(String hash) {
        String longUrl = redisTemplate.opsForValue().get(hash);
        return Optional.ofNullable(longUrl);
    }
}
