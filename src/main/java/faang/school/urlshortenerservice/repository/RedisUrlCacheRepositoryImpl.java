package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisUrlCacheRepositoryImpl implements UrlRepository {
    private final RedisOperations<String, String> redisTemplate;

    @Override
    @Transactional
    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl);
    }

    @Override
    @Transactional
    public Optional<String> findUrlByHash(String hash) {
        String longUrl = redisTemplate.opsForValue().get(hash);
        if (longUrl == null) {
            return Optional.empty();
        } else {
            return Optional.of(longUrl);
        }
    }
}
