package faang.school.urlshortenerservice.repository.url.impl;

import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl implements UrlCacheRepository {

    private final RedisTemplate<String, Url> redisUrlTemplate;

    @Override
    public void save(Url url) {
        redisUrlTemplate.opsForValue().set(url.getHash(), url);
    }

    @Override
    public Optional<Url> findUrlInCacheByHash(String hash) {
        return Optional.ofNullable(redisUrlTemplate.opsForValue().get(hash));
    }
}
