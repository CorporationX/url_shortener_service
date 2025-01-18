package faang.school.urlshortenerservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.model.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void save(Url url) {
        log.info("Trying to save url {} with the hash {}", url, url.getHash());
        redisTemplate.opsForValue().set(url.getHash(), url, 1, TimeUnit.DAYS);
    }

    public Url getByHash(String hash) {
        log.info("Trying to get url with hash {}", hash);
        return objectMapper.convertValue(redisTemplate.opsForValue().get(hash), Url.class);
    }
}
