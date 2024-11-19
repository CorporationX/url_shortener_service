package faang.school.urlshortenerservice.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlRedisCacheService {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String url) {
        try {
            redisTemplate.opsForValue().set(hash, url);
        } catch (Exception exception) {
            log.info("Can not save url to Redis");
        }
    }

    public Optional<String> get(String hash) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
        } catch (Exception exception) {
            log.info("Can not find url in Redis");
            return Optional.empty();
        }
    }

    public void delete(String hash) {
        redisTemplate.delete(hash);
    }
}
