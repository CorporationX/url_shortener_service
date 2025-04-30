package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UrlCacheRepository {
    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<Void> save(Url url) {
        return redisTemplate.opsForValue().set(url.getHash(), url.getUrl()).then();
    }

    public Mono<Void> saveAll(List<Url> urls) {
        Map<String, String> mapUrl = urls.stream()
                .collect(Collectors.toMap(Url::getHash, Url::getUrl));
        return redisTemplate.opsForValue().multiSet(mapUrl).then();
    }

    public Mono<String> get(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
