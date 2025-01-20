package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {
    private final RedisTemplate<String, Url> urlRedisTemplate;

    public void save(Url url) {
        try {
            urlRedisTemplate.opsForValue().set(url.getHash(), url, 7, TimeUnit.DAYS);
        } catch (JedisConnectionException exception) {
            log.error("{}", exception.getMessage(), exception);
        }
    }

    public Optional<Url> findByHash(String hash) {
        try {
            return Optional.ofNullable(urlRedisTemplate.opsForValue().get(hash));
        } catch (JedisConnectionException exception) {
            log.error("{}", exception.getMessage(), exception);
            return Optional.empty();
        }
    }

    public void deleteAll(List<String> hashes) {
        urlRedisTemplate.delete(hashes);
    }
}
