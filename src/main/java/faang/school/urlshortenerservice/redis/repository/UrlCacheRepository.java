package faang.school.urlshortenerservice.redis.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UrlCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public UrlCacheRepository(@Qualifier("redisTemplateBean") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl);
    }
}
