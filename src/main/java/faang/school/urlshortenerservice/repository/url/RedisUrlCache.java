package faang.school.urlshortenerservice.repository.url;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisUrlCache implements UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl);
    }

    @Override
    public String find(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
