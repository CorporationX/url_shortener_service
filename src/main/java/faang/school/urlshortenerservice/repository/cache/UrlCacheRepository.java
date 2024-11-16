package faang.school.urlshortenerservice.repository.cache;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {
    private final RedisTemplate<String, Url> urlRedisTemplate;

    public void saveTtlInHour(Url url, long ttlInHour) {
        try {
            urlRedisTemplate.opsForValue().set(url.getHash(), url, Duration.ofHours(ttlInHour));
        } catch (JedisConnectionException exception) {
            log.error("{}", exception.getMessage(), exception);
        }
    }

    public Url findByHash(String hash) {
        try {
            return urlRedisTemplate.opsForValue().get(hash);
        } catch (JedisConnectionException exception) {
            log.error("{}", exception.getMessage(), exception);
            return null;
        }
    }

    public void deleteAll(List<String> hashes) {
        urlRedisTemplate.delete(hashes);
    }
}
