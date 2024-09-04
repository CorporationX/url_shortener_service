package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisUrlCacheRepository implements UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(Url url) {
        redisTemplate.opsForValue().set(url.getHash(), url.getUrl());
    }

    @Override
    public Optional<String> findLongUrlByHash(String hash) {
        String longUrl = redisTemplate.opsForValue().get(hash);
        return Optional.ofNullable(longUrl);
    }

    @Override
    public void delete(String hash) {
        redisTemplate.delete(hash);
    }
}
