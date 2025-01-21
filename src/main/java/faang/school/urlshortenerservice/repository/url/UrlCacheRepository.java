package faang.school.urlshortenerservice.repository.url;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @SneakyThrows
    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
}
