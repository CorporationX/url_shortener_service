package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.redis.properties.RedisCacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisCacheProperties redisCacheProperties;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(
                formKey(hash),
                url,
                redisCacheProperties.timeToLive()
        );
    }

    public Optional<String> get(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(formKey(hash)));
    }

    public void deleteBatch(List<String> hashes) {
        List<String> keys = hashes.stream()
                .map(this::formKey)
                .toList();
        redisTemplate.delete(keys);
    }

    private String formKey(String hash) {
        return String.format("%s::%s", redisCacheProperties.name(), hash);
    }
}
