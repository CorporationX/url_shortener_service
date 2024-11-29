package faang.school.urlshortenerservice.repository.url;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    
    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "url:";
    
    public Optional<String> findUrlByHash(String hash) {
        String url = redisTemplate.opsForValue().get(KEY_PREFIX + hash);
        return Optional.ofNullable(url);
    }
    
    public void saveUrl(String hash, String url) {
        redisTemplate.opsForValue().set(KEY_PREFIX + hash, url);
    }
}
