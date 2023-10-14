package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final StringRedisTemplate stringRedisTemplate;

    public void save(String hash, String url) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set(hash, url);
    }
}
