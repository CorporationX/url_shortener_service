package faang.school.urlshortenerservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(Url url) {
        redisTemplate.opsForValue().set(url.getHash(), url.getUrl());
    }

    public Optional<Url> get(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        Url originalUrl = Url.builder()
                .url(url)
                .hash(hash)
                .build();
        return Optional.of(originalUrl);
    }
}