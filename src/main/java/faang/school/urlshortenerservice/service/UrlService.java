package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    public String getOriginalUrl(String hash) {
        log.info("Looking for URL associated with hash: {}", hash);

        String originalUrl = urlCacheRepository.getUrlByHash(hash);
        if (originalUrl == null) {
            log.info("URL not found in cache. Checking database...");
            originalUrl = urlRepository.findUrlByHash(hash);
        }

        if (originalUrl == null) {
            log.error("URL not found for hash: {}", hash);
            throw new UrlNotFoundException("URL not found for hash: " + hash);
        }

        log.info("Returning URL for hash {}: {}", hash, originalUrl);
        return originalUrl;
    }
}