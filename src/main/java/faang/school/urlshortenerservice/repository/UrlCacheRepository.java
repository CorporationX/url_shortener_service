package faang.school.urlshortenerservice.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
@AllArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void saveAssociation(String url, String hash){
        redisTemplate.opsForValue().set(hash, url);
    }

    public Pair<String, String> getAssociation(String hash){
        return Pair.of(Objects.requireNonNull(redisTemplate.opsForValue().get(hash)), hash);
    }
}
