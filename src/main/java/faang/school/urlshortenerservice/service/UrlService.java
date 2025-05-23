package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortenUrlRequest;
import faang.school.urlshortenerservice.dto.ShortenedUrlResponse;
import faang.school.urlshortenerservice.dto.UrlCreatedEvent;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.UrlMapping;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${hash.url.base-url}")
    @NotBlank(message = "Base URL prefix must be configured.")
    private String urlPrefix;

    @Transactional
    public ShortenedUrlResponse shortenUrl(ShortenUrlRequest url) {
        String originalUrl = url.originalUrl();
        log.info("Starting URL shortening for: {}", originalUrl);

        String hash = hashCache.getHash();

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setHashValue(hash);
        urlMapping.setOriginalUrl(originalUrl);
        urlRepository.save(urlMapping);

        eventPublisher.publishEvent(new UrlCreatedEvent(hash, originalUrl));

        String shortenedUrl = buildShortUrl(hash);
        log.info("Built short URL: {}", shortenedUrl);
        return new ShortenedUrlResponse(shortenedUrl);
    }

    @Transactional
    public String getOriginalUrl(String hash) {
        return urlCacheRepository.findUrlByHash(hash)
                .orElseGet(() -> {
                            String originalUrl = urlRepository.findOriginalUrlByHash(hash)
                                    .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash));
                            eventPublisher.publishEvent(new UrlCreatedEvent(hash, originalUrl));
                            return originalUrl;
                        }
                );
    }

    private String buildShortUrl(String hash) {
        return urlPrefix + hash;
    }
}