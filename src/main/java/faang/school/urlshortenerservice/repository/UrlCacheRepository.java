package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {

    protected final RedisTemplate<String, Object> redisTemplate;

    public void saveUrlInCache(String hash, Url url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    public Url getUrlFromCache(String hash) {
        return (Url) redisTemplate.opsForValue().get(hash);
    }

}
