package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisUrlTemplate;

    public void save(String hash, String url) {
        redisUrlTemplate.opsForValue().set(hash, url);
    }

    public Optional<String> get(String hash) {
        String hashCache = redisUrlTemplate.opsForValue().get(hash);
        if (hashCache != null) {
            log.warn("Хеш {} найден в кеше", hash);
        } else {
            log.warn("Хэш {} не найден в кеше", hash);
        }
        return hashCache == null ? Optional.empty() : Optional.of(hashCache);
    }

}