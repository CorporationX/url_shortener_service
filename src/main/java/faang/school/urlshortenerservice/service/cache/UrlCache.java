package faang.school.urlshortenerservice.service.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlCache {

    private final StringRedisTemplate redisTemplate;

    public void saveUrlMapping(String hash, String longUrl) {
        redisTemplate.opsForValue().set("url:" + hash, longUrl);
    }

    public String getLongUrl(String hash) {
        return redisTemplate.opsForValue().get("url:" + hash);
    }
}
