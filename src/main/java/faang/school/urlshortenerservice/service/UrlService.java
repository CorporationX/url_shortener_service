package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.model.UrlEntity;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Value("${url-shortener.base-url}")
    private String baseUrl;

    @Transactional
    public String shortenUrl(String originalUrl) {
        log.debug("Shortening URL: {}", originalUrl);

        // Check if URL already shortened
        var existingUrl = urlRepository.findByOriginalUrl(originalUrl);
        if (existingUrl.isPresent()) {
            String hash = existingUrl.get().getHash();
            log.debug("URL already exists with hash: {}", hash);
            return buildShortUrl(hash);
        }

        // Get new hash from cache
        String hash = hashCache.getHash();
        if (hash == null) {
            throw new RuntimeException("Unable to get hash from cache");
        }

        log.debug("Generated hash for URL: {}", hash);

        // Save to database
        UrlEntity urlEntity = new UrlEntity(hash, originalUrl);
        urlRepository.save(urlEntity);
        log.debug("Saved URL entity to database");

        // Save to Redis cache
        urlCacheRepository.save(hash, originalUrl);
        log.debug("Saved URL mapping to Redis cache");

        return buildShortUrl(hash);
    }

    public String getOriginalUrl(String hash) {
        log.debug("Looking up original URL for hash: {}", hash);

        // First try to get URL from Redis cache
        var cachedUrl = urlCacheRepository.findByHash(hash);
        if (cachedUrl.isPresent()) {
            log.debug("Found URL in cache: {}", cachedUrl.get());
            return cachedUrl.get();
        }

        log.debug("URL not found in cache, checking database");

        // If not in cache, try database
        var urlEntity = urlRepository.findById(hash);
        if (urlEntity.isPresent()) {
            String originalUrl = urlEntity.get().getOriginalUrl();
            // Add to cache for future requests
            urlCacheRepository.save(hash, originalUrl);
            log.debug("Found URL in database and saved to cache: {}", originalUrl);
            return originalUrl;
        }

        // URL not found anywhere
        log.warn("No URL found for hash: {}", hash);
        throw new faang.school.urlshortenerservice.exception.UrlNotFoundException(hash);
    }

    private String buildShortUrl(String hash) {
        return baseUrl + "/" + hash;
    }
}
