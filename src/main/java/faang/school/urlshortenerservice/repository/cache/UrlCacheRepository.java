package faang.school.urlshortenerservice.repository.cache;

import faang.school.urlshortenerservice.entity.Url;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, Url> urlRedisTemplate;

    public void saveByTtlInHour(Url url, long ttlInHour) {
        urlRedisTemplate.opsForValue().set(url.getHash(), url, Duration.ofHours(ttlInHour));
    }

    public Optional<Url> findByHash(String hash) {
        return Optional.ofNullable(urlRedisTemplate.opsForValue().get(hash));
    }

    public void deleteAllByHashes(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            return;
        }
        urlRedisTemplate.delete(hashes);
    }
}