package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisUrlTemplate;

    public void putUrl(String hash, String url) {
        redisUrlTemplate.opsForValue().set(hash, url);
    }

    public Optional<String> getUrl(String hash) {
        return Optional.ofNullable(redisUrlTemplate.opsForValue().get(hash));
    }
}
