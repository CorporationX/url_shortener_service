package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.dto.UrlDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    protected final RedisTemplate<String, Object> redisTemplate;

    public void saveUrlInCache(String hash, UrlDto urlDto) {
        redisTemplate.opsForValue().set(hash, urlDto.getUrl());
    }

    public UrlDto getUrlFromCache(String hash) {
        return (UrlDto) redisTemplate.opsForValue().get(hash);
    }
}
