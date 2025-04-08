package faang.school.urlshortenerservice.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCache {
    private final RedisTemplate<String, String> redisTemplate;

    public void cacheUrl(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    public String getUrlFromCache(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        return url;
    }
}
