package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.dto.UrlDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.cache.redis.time-to-live}")
    private int ttl;

    public void save(UrlDto urlDto) {
        redisTemplate.opsForValue().set(urlDto.getUrl(), urlDto.getHash(), ttl);
        redisTemplate.opsForValue().set(urlDto.getHash(), urlDto.getUrl(), ttl);
        log.info("Put into cache: {}", urlDto);
    }

    public String getHashByUrl(String url) {
        return redisTemplate.opsForValue().get(url);
    }


    public String getUrlByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}