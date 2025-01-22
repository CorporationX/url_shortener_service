package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {

    private final RedisTemplate<String, Url> redisTemplate;

    public void saveUrlInCache(String hash, Url url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    public Url getUrlFromCache(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }

}
