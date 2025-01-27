package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.UrlAssociation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public String getOriginUrl(String hash) {
       return redisTemplate.opsForValue().get(hash);
    }

    public void save(UrlAssociation urlAssociation) {
        redisTemplate.opsForValue().set(urlAssociation.getHash(), urlAssociation.getUrl());
    }
}
