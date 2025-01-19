package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.app.AppProperties;
import faang.school.urlshortenerservice.config.redis.RedisProperties;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final RedisProperties redisProperties;
    private final AppProperties appProperties;

    public String createShortLink(String originalUrl) {
        String hash = hashCache.takeCache();
        Url entity = createUrlEntity(hash, originalUrl);
        urlRepository.save(entity);
        urlCacheRepository.saveWithTTL(hash, originalUrl, redisProperties.timeToLiveInMinutes());
        String shortUrl = String.format("%s/%s", appProperties.baseUrl(), hash);
        log.info("Created short link '{}' for url: {}", shortUrl, originalUrl);
        return shortUrl;
    }

    public String getOriginalUrl(String hash) {
        Optional<String> url = urlCacheRepository.getValue(hash);
        if (url.isPresent()) {
            log.info("Found original url '{}' for hash '{}', send to redirect", url, hash);
            return url.get();
        }
        log.info("Original url in cache not found for hash '{}' and will be retrieved from database", hash);
        Url entity = findUrlByHash(hash);
        return entity.getUrl();
    }

    private Url findUrlByHash(String hash) {
        return urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Url with hash %s not found", hash)));
    }

    private Url createUrlEntity(String hash, String originalUrl) {
        return Url.builder()
                .hash(hash)
                .url(originalUrl)
                .build();
    }
}
