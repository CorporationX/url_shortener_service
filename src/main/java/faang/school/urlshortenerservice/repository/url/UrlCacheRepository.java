package faang.school.urlshortenerservice.repository.url;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository  {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    public String getByHash(String hash) {
        return (String) redisTemplate.opsForValue().get(hash);
    }
}

