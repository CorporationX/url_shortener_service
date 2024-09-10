package faang.school.urlshortenerservice.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.entity.URL;
import faang.school.urlshortenerservice.exception.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class URLCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.cache.redis.time-to-live}")
    private long timeToLive;

    public void save(URL url) {
        try {
            String urlJson = objectMapper.writeValueAsString(url);
            redisTemplate.opsForValue()
                    .set(url.getHash(), urlJson, timeToLive, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            log.error(ExceptionMessage.SERIALIZATION_IN_OBJECT, url);
        }
    }

    public URL find(String hash) {
        String urlJson = (String) redisTemplate.opsForValue().get(hash);
        try {
            return objectMapper.readValue(urlJson, URL.class);
        } catch (JsonProcessingException e) {
            log.error(ExceptionMessage.DESERIALIZATION_IN_OBJECT, urlJson);
            return null;
        }
    }
}