package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.CreateShortUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.CachedUrl;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    @Value("${server.domain}")
    private String domain;

    @Value("${request_mapping.url_controller:/api/v1/urls}")
    private String requestMapping;

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Override
    @Transactional
    public ShortUrlDto createShortUrl(CreateShortUrlDto createShortUrlDto) {
        String freeHash = hashCache.getFreeHash();
        urlRepository.save(new Url(freeHash, createShortUrlDto.originalUrl()));
        urlCacheRepository.save(new CachedUrl(freeHash, createShortUrlDto.originalUrl()));
        log.info("Created new shortUrl for {}", createShortUrlDto.originalUrl());
        return new ShortUrlDto(concatenateShortUrl(freeHash));
    }

    @Override
    public String getOriginalUrl(String key) {
        Optional<CachedUrl> keyValuePairFromCache = urlCacheRepository.findById(key);
        if (keyValuePairFromCache.isEmpty()) {
            return getOriginalUrlFromDb(key);
        }
        return keyValuePairFromCache.get().getUrl();
    }

    private String getOriginalUrlFromDb(String key) {
        Url urlFromDb = urlRepository.findByHash(key).orElseThrow(() -> new InvalidUrlException("URL is invalid"));
        String originalUrl = urlFromDb.getOriginalUrl();
        urlCacheRepository.save(new CachedUrl(key, originalUrl));
        log.warn("OriginalUrl: {} was not found in cache by shortUrl: {}, but it was found in db",
                originalUrl, key);
        return originalUrl;
    }

    private String concatenateShortUrl(String freeHash) {
        StringBuilder result = new StringBuilder();
        result.append(domain);
        result.append(requestMapping);
        result.append("/");
        result.append(freeHash);
        return result.toString();
    }
}
