package faang.school.urlshortenerservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCache {

    private final StringRedisTemplate redisTemplate;

    public void save(String longUrl, String hash) {
        redisTemplate.opsForValue().set(hash, longUrl);
    }

    public Optional<String> getUrlByHash(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }
}