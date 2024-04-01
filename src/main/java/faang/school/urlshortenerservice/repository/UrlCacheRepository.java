package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String urlDto) {
        redisTemplate.opsForValue().set(hash, urlDto);
    }

    public String getUrlByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
