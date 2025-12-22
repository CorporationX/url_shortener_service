package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.dto.URLCacheDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisURLCacheRepository {
    private static final String CACHE_KEY_PREFIX = "url:";
    private static final long CACHE_TTL_HOURS = 24;

    private final RedisTemplate<String, URLCacheDto> redisTemplate;

    public RedisURLCacheRepository(RedisTemplate<String, URLCacheDto> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String hash, String originalUrl) {
        URLCacheDto data = URLCacheDto.builder()
                .hash(hash)
                .originalUrl(originalUrl)
                .build();
        redisTemplate.opsForValue().set(
                CACHE_KEY_PREFIX + hash,
                data,
                CACHE_TTL_HOURS,
                TimeUnit.HOURS
        );
    }

    public Optional<String> getByHash(String hash) {
        URLCacheDto data = redisTemplate.opsForValue().get(CACHE_KEY_PREFIX + hash);
        return data != null ? Optional.of(data.getOriginalUrl()) : Optional.empty();
    }

    public void delete(String hash) {
        redisTemplate.delete(CACHE_KEY_PREFIX + hash);
    }
}
