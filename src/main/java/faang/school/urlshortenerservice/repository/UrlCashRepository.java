package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCashRepository {
    private final StringRedisTemplate redisTemplate;

    public void save(String hash, String url) {
        ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
        ops.set(hash, url, 7, TimeUnit.DAYS);
    }

    public String getUrl(String hash) {
        ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
        return ops.get(hash);
    }
}
