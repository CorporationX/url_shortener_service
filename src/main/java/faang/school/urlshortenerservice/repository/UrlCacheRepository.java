package faang.school.urlshortenerservice.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UrlCacheRepository {

    private final ValueOperations<String, String> valueOperations;

    public UrlCacheRepository(StringRedisTemplate redisTemplate) {
        this.valueOperations = redisTemplate.opsForValue();
    }

    public void save(String hash, String url) {
        valueOperations.set(hash, url);
    }

    public Optional<String> get(String hash) {
        String url = valueOperations.get(hash);
        return Optional.ofNullable(url);
    }
}
