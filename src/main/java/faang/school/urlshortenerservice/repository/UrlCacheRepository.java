package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.dto.UrlDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(UrlDto urlDto) {
        redisTemplate.opsForValue().set(urlDto.getUrl(), urlDto.getHash());
    }

    public String getHashByUrl(String url) {
        return redisTemplate.opsForValue().get(url);
    }
}