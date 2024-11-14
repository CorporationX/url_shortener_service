package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {
    private final RedisTemplate<String, String> urlRedisTemplate;

    public void save(Url url) {
        urlRedisTemplate.opsForValue().set(url.getHash(), url.getUrl());
    }

    public String findByHash(String hash) {
        return urlRedisTemplate.opsForValue().get(hash);
    }
}
