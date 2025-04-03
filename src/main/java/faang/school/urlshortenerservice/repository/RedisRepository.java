package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.repository.api.CacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisRepository implements CacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    public Optional<String> findByHash(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }

    public void removeHashes(List<String> hashes) {
        redisTemplate.delete(hashes);
    }
}
