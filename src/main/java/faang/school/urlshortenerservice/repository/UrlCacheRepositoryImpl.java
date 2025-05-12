package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl implements UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "url:";

    @Override
    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(PREFIX + hash, url);
    }

    @Override
    public Optional<String> find(String hash) {
        String url = redisTemplate.opsForValue().get(PREFIX + hash);
        return Optional.ofNullable(url);
    }
}