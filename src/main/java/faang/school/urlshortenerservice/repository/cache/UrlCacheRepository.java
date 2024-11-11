package faang.school.urlshortenerservice.repository.cache;

import faang.school.urlshortenerservice.annotation.redis.IsRedisConnected;
import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {
    private final RedisTemplate<String, Url> urlRedisTemplate;

    @IsRedisConnected
    public void saveTtlInHour(Url url, long ttlInHour) {
        urlRedisTemplate.opsForValue().set(url.getHash(), url, Duration.ofHours(ttlInHour));
    }

    @IsRedisConnected
    public Url findByHash(String hash) {
        return urlRedisTemplate.opsForValue().get(hash);
    }

    public void deleteAll(List<String> hashes) {
        urlRedisTemplate.delete(hashes);
    }
}
