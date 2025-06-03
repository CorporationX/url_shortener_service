package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j

@Repository
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl implements UrlCacheRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String hash, String url) {
    redisTemplate.opsForValue().set(hash, url);
    log.info("Url saved to cache: {} -> {}", hash, url);
    }

    public Optional<String> findOriginalUrl(String hash) {
        String value = redisTemplate.opsForValue().get(hash);
        return Optional.ofNullable(value);
    }
}
