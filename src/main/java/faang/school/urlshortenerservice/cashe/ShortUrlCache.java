package faang.school.urlshortenerservice.cashe;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ShortUrlCache {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveUrl(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl);
    }
}
