package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@AllArgsConstructor
public class UrlCacheRepository {
    private RedisTemplate<String, Object> redisTemplate;

    public void saveUrlForTime(String hash, Url url, int time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(hash, url, time, timeUnit);
    }

    public Optional<Url> getUrl(String hash) {
        return Optional.of((Url) redisTemplate.opsForValue().get(hash));
    }
}
