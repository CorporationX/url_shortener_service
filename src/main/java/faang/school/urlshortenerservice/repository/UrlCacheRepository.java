package faang.school.urlshortenerservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.config.context.RedisConfig;
import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void save(Url url) {
        redisTemplate.opsForValue().set(url.getUrl(), url.getHash());
    }


}
