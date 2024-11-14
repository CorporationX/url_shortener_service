package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final StringRedisTemplate redisTemplate;

    public Optional<String> getUrlByHash(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        return Optional.ofNullable(url);
    }

    public void cacheUrl(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
    }
}