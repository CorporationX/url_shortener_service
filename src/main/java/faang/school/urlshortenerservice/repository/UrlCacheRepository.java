package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redis;

    public static final Duration TTL = Duration.ofHours(1);

    public Optional<String> find(String hash){
        return Optional.ofNullable(redis.opsForValue().get(hash));
    }

    public void save(String hash, String url) {
        redis.opsForValue().set(hash, url, TTL);
    }
}
