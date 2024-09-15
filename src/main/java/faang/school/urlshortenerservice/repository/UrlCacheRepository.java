package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<Hash, Url> redisTemplate;

    public void save(Hash hash, Url url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    public Optional<Url> findByHash(Hash hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }
}
