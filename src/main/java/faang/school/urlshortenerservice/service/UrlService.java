package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCache urlCache;

    @Value("${base.short-url}")
    private String baseShortUrl;

    @Transactional
    public String createShortUrl(String originalUrl) {
        Optional<String> existingHash = urlRepository.findByUrl(originalUrl);
        if (existingHash.isPresent()) {
            log.info("Found existing hash for URL: {}", originalUrl);
            return baseShortUrl + existingHash.get();
        }

        try {
            String hash = hashCache.getHash();

            Url url = Url.builder()
                    .url(originalUrl)
                    .hash(hash)
                    .build();
            urlRepository.save(url);

            urlCache.saveUrlMapping(hash, originalUrl);

            log.info("Created short URL for: {}", originalUrl);
            return baseShortUrl + hash;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Hash generation interrupted", e);
            throw new RuntimeException("Hash generation interrupted", e);
        }
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {

        String cachedUrl = urlCache.getLongUrl(hash);
        if (cachedUrl != null) {
            log.info("Found URL in cache for hash: {}", hash);
            return cachedUrl;
        }

        Url urlEntity = urlRepository.findByHash(hash);
        if (urlEntity != null) {
            urlCache.saveUrlMapping(hash, urlEntity.getUrl());
            log.info("Found URL in DB for hash: {}", hash);
            return urlEntity.getUrl();
        }

        log.error("URL not found for hash: {}", hash);
        throw new UrlNotFoundException("URL not found for hash: " + hash);
    }
}