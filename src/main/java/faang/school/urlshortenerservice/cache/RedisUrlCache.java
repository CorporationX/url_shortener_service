package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RedisUrlCache implements UrlCache {

    private final RedisTemplate<String, String> redisTemplate;
    private final UrlRepository urlRepository;

    @PostConstruct
    public void init() {
        redisTemplate.opsForValue().multiSet(
                urlRepository.findAll().stream()
                        .map(u -> Map.entry(u.getShortUrl(), u.getUrl()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    @Override
    public void saveUrl(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    @Override
    public Optional<String> getUrl(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        if (url == null) {
            Optional<Url> u = urlRepository.findByShortUrl(hash);
            if (u.isEmpty()) {
                return Optional.empty();
            }
            url = u.get().getUrl();
            saveUrl(hash, url);
        }
        return Optional.of(url);
    }
}
