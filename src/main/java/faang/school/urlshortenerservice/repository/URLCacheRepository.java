package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class URLCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.url-key}")
    private String cachedUrlKey;
    public void save(String key, String value) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(key, value);

        redisTemplate.opsForHash().putAll(cachedUrlKey, hashMap);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable((String) redisTemplate.opsForHash().get(cachedUrlKey, key));
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
