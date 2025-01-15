package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> template;

    public String searchInRedis(String hash) {
        return template.opsForValue().get(hash);
    }

    public void addToRedis(Url url) {
        template.opsForValue().set(url.getHash(), url.getUrl());
    }
}
