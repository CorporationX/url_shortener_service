package faang.school.urlshortenerservice.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void saveAssociation(String url, String hash) {
        redisTemplate.opsForValue().set(hash, url);
        log.info("Url {} and hash {} was saved successfully in redis", url, hash);
    }

    public Optional<Pair<String, String>> getAssociation(String hash) {
        log.info("trying to find hash {} in redis", hash);
        return Optional.of(Pair.of(Objects.requireNonNull(redisTemplate.opsForValue().get(hash)), hash));
    }
}
