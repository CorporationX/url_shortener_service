package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.properties.short_url.BaseUrlProperties;
import faang.school.urlshortenerservice.properties.short_url.UrlCacheProperties;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlUtil urlUtil;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final BaseUrlProperties baseUrlProperties;
    private final UrlCacheProperties urlCacheProperties;

    @Transactional
    public String createShortUrl(UrlDto urlDto) {
        log.info("Creating short URL for long URL: {}", urlDto.getUrl());
        validateUrl(urlDto.getUrl());

        String freeHash = hashCache.getHash();
        urlRepository.save(createUrlEntity(urlDto.getUrl(), freeHash));
        String shortUrl = "%s/%s".formatted(createBaseUrl(), freeHash);
        saveToDefaultCache(freeHash, urlDto.getUrl());

        log.info("Short URL={} for original URL={} was created!", shortUrl, urlDto.getUrl());
        return shortUrl;
    }

    public String getOriginalUrl(String hash) {
        log.info("Received request to get original URL for hash={}", hash);
        String originalUrl = urlCacheRepository.getOriginalUrl(hash)
                .orElseGet(() -> {
                    String originalUrlFromDb = getOriginalUrlFromDb(hash);
                    saveToDefaultCache(hash, originalUrlFromDb);
                    return originalUrlFromDb;
                });
        urlCacheRepository.updateShortUrlRequestStats(hash);
        log.info("Found original URL={} for hash={}", originalUrl, hash);
        return urlUtil.ensureUrlHasProtocol(originalUrl);
    }

    public List<Url> findUrlEntities(Set<String> urlHashes) {
        return urlRepository.findByHashSet(urlHashes);
    }

    private String getOriginalUrlFromDb(String hash) {
        return urlRepository.findOriginalUrlByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("Original URL not found for hash: %s".formatted(hash)));
    }

    private void saveToDefaultCache(String hash, String originalUrl) {
        urlCacheRepository.save(hash, originalUrl, urlCacheProperties.getDefaultTtlMinutes(), TimeUnit.MINUTES);
    }

    private void validateUrl(String url) {
        String urlWithProtocol = urlUtil.ensureUrlHasProtocol(url);
        if (!urlUtil.isValidUrl(urlWithProtocol)) {
            throw new DataValidationException("Invalid url!");
        }
    }

    private String createBaseUrl() {
        return "%s/%s".formatted(baseUrlProperties.getDomain(), baseUrlProperties.getPath());
    }

    private Url createUrlEntity(String url, String hash) {
        return Url.builder()
                .url(url)
                .hash(hash)
                .build();
    }
}
