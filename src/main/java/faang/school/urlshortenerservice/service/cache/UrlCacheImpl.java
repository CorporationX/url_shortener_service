package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class UrlCacheImpl implements UrlCache {
    private static final String PREFIX = "urls:";

    private final StringRedisTemplate cache;
    private final UrlRepository urlRepository;
    @Value("${url.cache.ttl}")
    private int ttl;

    String getKey(String hash) {
        return PREFIX + hash;
    }

    @Override
    public String get(String hash) {
        String url = cache.opsForValue().get(getKey(hash));
        if (url == null || url.isBlank()) {
            url = urlRepository.findByIdOrThrow(hash).getUrl();
            set(hash, url);
        }
        return url;
    }

    @Override
    public void set(String hash, String url) {
        cache.opsForValue().set(getKey(hash), url, ttl, TimeUnit.SECONDS);
    }

    @Override
    public void delete(String hash) {
        cache.opsForValue().getOperations().delete(getKey(hash));
    }

    @Override
    public void deleteAll(List<String> hashes) {
        cache.opsForValue()
                .getOperations()
                .delete(hashes.stream()
                        .map(this::getKey)
                        .toList());
    }
}