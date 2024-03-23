package faang.school.urlshortenerservice.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisUrlTemplate;
    private final ObjectMapper objectMapper;
    private final UrlMapper urlMapper;

    public void save(String hash, String url) {
        redisUrlTemplate.opsForValue().set(hash, url);
    }

    public Optional<Url> get(String hash) {
        String json = redisUrlTemplate.opsForValue().get(hash);
        try {
            UrlDto dto = objectMapper.readValue(json, UrlDto.class);
            return Optional.of(urlMapper.toEntity(dto));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

}