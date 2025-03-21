package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public void save(String hash, String url) {
        stringRedisTemplate.opsForValue().set(hash, url);
    }

    public String getByHash(String hash) {
        return stringRedisTemplate.opsForValue().get(hash);
    }
}
