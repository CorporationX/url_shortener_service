package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Value("${app.base-url:https://sh.com/}")
    private String baseUrl;

    public UrlResponseDto createShortUrl(String originalUrl) {
        log.debug("Creating short URL for: {}", originalUrl);

        String hash = hashCache.getHash();
        log.debug("Generated hash: {}", hash);

        Url urlEntity = new Url(hash, originalUrl, LocalDateTime.now());

        urlRepository.save(urlEntity);
        log.debug("Saved URL to database with hash: {}", hash);

        urlCacheRepository.saveUrl(hash, originalUrl);
        log.debug("Saved URL to Redis cache with hash: {}", hash);

        String shortUrl = baseUrl + hash;

        return new UrlResponseDto(shortUrl, hash);
    }

    public String getOriginalUrl(String hash) {
        log.debug("Looking for URL with hash: {}", hash);

        Optional<String> cachedUrl = urlCacheRepository.findUrlByHash(hash);
        if (cachedUrl.isPresent()) {
            log.debug("Found URL in Redis cache for hash: {}", hash);
            return cachedUrl.get();
        }

        Optional<Url> urlEntity = urlRepository.findByHash(hash);
        if (urlEntity.isPresent()) {
            String originalUrl = urlEntity.get().getUrl();
            log.debug("Found URL in database for hash: {}", hash);

            urlCacheRepository.saveUrl(hash, originalUrl);

            return originalUrl;
        }

        log.warn("URL not found for hash: {}", hash);
        throw new UrlNotFoundException("URL not found for hash: " + hash);
    }
}
