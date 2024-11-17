package faang.school.urlshortenerservice.repository.impl;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl implements UrlCacheRepository {

    private final RedisTemplate<String, String> redisStringTemplate;

    @Override
    public void save(Url url) {
        redisStringTemplate.opsForValue().set(url.getHash(), url.getUrl());
    }

    @Override
    public String findUrlInCacheByHash(String hash) {
        return redisStringTemplate.opsForValue().get(hash);
    }
}
