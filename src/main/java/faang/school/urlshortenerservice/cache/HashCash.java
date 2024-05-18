package faang.school.urlshortenerservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashCash {

    private final RedisTemplate<String, String> redisTemplate;

    public String getHash(String longUrl) {
        return redisTemplate.opsForValue().get(longUrl);
    }

    public void putHash(String longUrl, String hash) {
        redisTemplate.opsForValue().set(longUrl, hash);
    }
}
