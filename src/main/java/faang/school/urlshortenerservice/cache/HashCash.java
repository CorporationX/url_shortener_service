package faang.school.urlshortenerservice.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class HashCash {

    private final RedisTemplate<String, String> redisTemplate;

    public HashCash(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getHash(String longUrl) {
        return redisTemplate.opsForValue().get(longUrl);
    }

    public void putHash(String longUrl, String hash) {
        redisTemplate.opsForValue().set(longUrl, hash);
    }
}
