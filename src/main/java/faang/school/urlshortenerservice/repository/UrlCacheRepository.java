package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository{
    private final StringRedisTemplate redisTemplate;
    private static final String KET_PREFIX = "url:";

    public String getUrl(String hash) {
        return redisTemplate.opsForValue().get(KET_PREFIX + hash);
    }

    public void putUrl(String hash, String url) {
        redisTemplate.opsForValue().set(KET_PREFIX + hash, url);
    }
}
