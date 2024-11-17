package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Url;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public UrlCacheRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(Url url) {
        redisTemplate.opsForValue().set(url.getHash(), url.getOriginalUrl());
    }

    public String get(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}