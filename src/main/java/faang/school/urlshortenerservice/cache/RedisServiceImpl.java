package faang.school.urlshortenerservice.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> void save(String key, T value) {
        log.info("Saving data to redis , key={}", key);

        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue);
        } catch (JsonProcessingException e) {
            log.error("Error saving value to Redis", e);
            e.printStackTrace();
        }
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> clazz) {
        log.info("Getting data from Redis, key={}", key);
        String jsonValue = redisTemplate.opsForValue().get(key);
        try {
            if (jsonValue != null) {
                return Optional.of(objectMapper.readValue(jsonValue, clazz));
            }
        } catch (IOException e) {
            log.error("Error reading value from Redis", e);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> get(String key, TypeReference<T> typeReference) {
        log.info("Getting data from Redis, key={}", key);
        String jsonValue = redisTemplate.opsForValue().get(key);
        try {
            if (jsonValue != null) {
                return Optional.of(objectMapper.readValue(jsonValue, typeReference));
            }
        } catch (IOException e) {
            log.error("Error reading value from Redis", e);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void delete(String key) {
        log.info("Deleting data from redis , key={}", key);
        redisTemplate.delete(key);
    }
}
