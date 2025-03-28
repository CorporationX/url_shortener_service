package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.TTL-HOURS}")
    private long TTL_HOURS;

    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofHours(TTL_HOURS));
        log.info("Set value in Redis for key " + key + " to " + value);
    }

    public Optional<String> getValue(String key) {
        Optional<String> value = Optional.ofNullable(redisTemplate.opsForValue().get(key));
        if (value.isPresent()) {
            redisTemplate.expire(key, Duration.ofHours(TTL_HOURS));
        }
        return value;
    }
}
