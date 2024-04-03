package faang.school.urlshortenerservice.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisUrlTemplate;
    private final ObjectMapper objectMapper;

    public void save(Url url) {
        try {
            redisUrlTemplate
                    .opsForValue()
                    .set(url.getHash(), objectMapper.writeValueAsString(url));
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Optional<Url> get(String hash) {
        try {
            String json = redisUrlTemplate.opsForValue().get(hash);
            Url url = objectMapper.readValue(json, Url.class);
            return Optional.of(url);
        } catch (JsonProcessingException exception) {
            return Optional.empty();
        }
    }
}