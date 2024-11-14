package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {

    private final RedisTemplate<String, Url> redisTemplate;

    public void save(String hash, Url url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    public Optional<Url> getUrl(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }

}
