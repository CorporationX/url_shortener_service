package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.CreateUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import faang.school.urlshortenerservice.service.cache.UrlCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlServiceImpl implements UrlService {
    private final HashCache cache;
    private final UrlRepository urlRepository;
    private final UrlCache urlCache;
    @Value("${url.domain}")
    private String domain;

    @Override
    public String createShortUrl(CreateUrlDto dto) {
        try {
            log.debug("Creating short URL for original URL: {}", dto.originalUrl());
            String hash = cache.getHash();
            log.debug("Generated hash: {}", hash);
            Url url = Url.builder()
                    .url(dto.originalUrl())
                    .hash(hash)
                    .build();
            urlRepository.save(url);
            log.debug("Saved URL entity with hash: {}", hash);
            urlCache.set(hash, dto.originalUrl());
            log.debug("Set cache entry for hash: {}", hash);
            String shortUrl = buildShortUrl(hash);
            log.info("Successfully created short URL: {}", shortUrl);
            return shortUrl;
        } catch (Exception e) {
            log.error("Failed to create short URL for: {}", dto.originalUrl(), e);
            throw new RuntimeException("Error creating short URL", e);
        }
    }

    @Override
    public String getOriginalUrl(String hash) {
        try {
            log.debug("Getting original URL for hash: {}", hash);
            String originalUrl = urlCache.get(hash);
            if (originalUrl == null) {
                log.warn("No original URL found for hash: {}", hash);
            } else {
                log.debug("Found original URL: {}", originalUrl);
            }
            return originalUrl;
        } catch (Exception e) {
            log.error("Failed to get original URL for hash: {}", hash, e);
            throw new RuntimeException("Error retrieving original URL", e);
        }
    }

    private String buildShortUrl(String hash) {
        return String.format("%s/%s", domain, hash);
    }
}