package faang.school.urlshortenerservice.repository.url;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    @Value("${hash.cache.url.prefix:url}")
    private String URL_CACHE_PREFIX;
    private final RedisTemplate<String, String> redisTemplate;

    @SneakyThrows
    public void save(String key, String value) {
        redisTemplate.opsForValue().set(URL_CACHE_PREFIX + "::" + key, value);
    }
}
