package faang.school.urlshortenerservice.repository.redis;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${hash.cache.ttl-seconds}")
    private long ttlSeconds;

    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(
            hash,
            longUrl,
            ttlSeconds,
            TimeUnit.SECONDS
        );
        log.debug("Hash saved: {}", hash);
    }

    public String findUrlByHash(String hash) {
        return (String) redisTemplate.opsForValue().get(hash);
    }

    public void deleteAll(List<String> hashes) {
        redisTemplate.delete(hashes);
    }
}
