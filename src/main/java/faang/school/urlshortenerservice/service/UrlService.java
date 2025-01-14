package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlCreateDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.properties.short_url.ShortUrlProperties;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCacheService;
import faang.school.urlshortenerservice.service.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.validator.UrlValidationHelper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlValidationHelper urlValidationHelper;
    private final HashCacheService hashCacheService;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final ShortUrlProperties shortUrlProperties;

    public String createShortUrl(ShortUrlCreateDto shortUrlCreateDto) {
        log.info("Creating short URL for long URL: {}", shortUrlCreateDto.getUrl());
        urlValidationHelper.validateUrl(shortUrlCreateDto.getUrl());

        String freeHash = hashCacheService.getHash();
        Url url = createUrlEntity(shortUrlCreateDto.getUrl(), freeHash);
        urlRepository.save(url);
        urlCacheRepository.save(freeHash, shortUrlCreateDto.getUrl());
        String shortUrl = "%s/%s".formatted(createBaseUrl(), freeHash);

        log.info("Short URL={} for original URL={} was created!", shortUrl, shortUrlCreateDto.getUrl());
        return shortUrl;
    }

    @Cacheable(value = "${short-url.cache-settings.default-key}", key = "#hash", cacheManager = "redisCacheManager")
    public String getOriginalUrl(String hash) {
        log.info("Received request to get original URL for hash={}", hash);
        String originalUrl = urlRepository.findOriginalUrlByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("Original URL not found for hash: %s".formatted(hash)));
        log.info("Found original URL={} for hash={}", originalUrl, hash);
        return originalUrl;
    }

    public List<Url> findUrlEntities(Set<String> urlHashes) {
        return urlRepository.findByHashSet(urlHashes);
    }

    private String createBaseUrl() {
        return "%s/%s".formatted(shortUrlProperties.getBaseDomain(), shortUrlProperties.getBasePath());
    }

    private Url createUrlEntity(String url, String hash) {
        return Url.builder()
                .url(url)
                .hash(hash)
                .build();
    }
}
