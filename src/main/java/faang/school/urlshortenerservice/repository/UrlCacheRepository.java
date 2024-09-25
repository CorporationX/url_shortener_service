package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void saveHashAndUrl(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    public Optional<String> getOriginalUrl(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }
}
