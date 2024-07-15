package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.dto.UrlDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String CACHE_KEY = "url_";

    public String getHash(UrlDto urlDto) {
        return redisTemplate.opsForValue().get(createCacheKey(urlDto));
    }

    public void saveHash(UrlDto urlDto, String hash) {
        redisTemplate.opsForValue().set(createCacheKey(urlDto), hash);
    }

    private String createCacheKey(UrlDto urlDto) {
        return CACHE_KEY + urlDto.getUrl();
    }
}
