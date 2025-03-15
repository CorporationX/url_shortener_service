package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final UrlRepository urlRepository;
    private final CacheManager cacheManager;

    @Cacheable(value = "url")
    public Url getUrl(String hash) {
        return urlRepository.getReferenceById(hash);
    }
}
