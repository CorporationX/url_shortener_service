package faang.school.urlshortenerservice.repository.redis;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long TTL_DAYS = 1;

    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl, TTL_DAYS, TimeUnit.HOURS);
        log.debug("Hash saved: {}", hash);
    }

    public String findUrlByHash(String hash) {
        return (String) redisTemplate.opsForValue().get(hash);
    }

    public void deleteBatch(List<String> oldHashes) {
        redisTemplate.delete(oldHashes);
    }
}
