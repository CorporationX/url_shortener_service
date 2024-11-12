package faang.school.urlshortenerservice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UrlCacheRepository {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl);
    }
}
