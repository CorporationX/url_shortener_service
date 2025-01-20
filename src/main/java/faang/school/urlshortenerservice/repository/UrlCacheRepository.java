package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public String findUrlByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }


    public void saveUrl(String url, String hash ) {
        redisTemplate.opsForValue().set(hash, url);
    }
}
