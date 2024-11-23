package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl implements UrlCacheRepository {

    private final RedisTemplate<String, Url> redisStringTemplate;

    @Override
    public void save(Url url) {
        redisStringTemplate.opsForValue().set(url.getHash(), url);
        log.info("save Url in Redis cache: {}", url);
    }

    @Override
    public Optional<Url> findUrlByHash(String hash) {
        return Optional.ofNullable(redisStringTemplate.opsForValue().get(hash));
    }
}
