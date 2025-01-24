package faang.school.urlshortenerservice.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl);
    }

    public String findUrlByHash(String hash) {
        return (String) redisTemplate.opsForValue().get(hash);
    }

    public void deleteBatch(List<String> oldHashes) {
        redisTemplate.delete(oldHashes);
    }
}
