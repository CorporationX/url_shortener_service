package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Value("${url-data.protocol}")
    private String protocol;

    @Value("${url-data.domain}")
    private String domain;

    @Value("${server.port}")
    private int port;


    @Value("${redis-time-to-live-seconds.new-url}")
    private long ttlNewUrl;

    @Value("${redis-time-to-live-seconds.requested-url}")
    private long ttlRequestedUrl;

    public String getOriginalUrl(String hash) {
        log.info("Looking for URL associated with hash: {}", hash);

        String originalUrl = urlCacheRepository.getUrlByHash(hash);
        if (originalUrl == null) {
            log.info("URL not found in cache. Checking database...");
            originalUrl = urlRepository.findUrlByHash(hash);

            if (originalUrl == null) {
                log.error("URL not found for hash: {}", hash);
                throw new UrlNotFoundException("URL not found for hash: " + hash);
            }

            urlCacheRepository.cacheUrl(hash, originalUrl, ttlRequestedUrl);
        }


        log.info("Returning URL for hash {}: {}", hash, originalUrl);
        return originalUrl;
    }

    @Transactional
    public String convertToShortUrl(String url) {
        log.info("Converting URL: {}", url);

        String hash = hashCache.getHash();

        urlRepository.saveUrl(hash, url);
        urlCacheRepository.cacheUrl(hash, url, ttlNewUrl);

        String shortUrl = String.format("%s://%s:%d/%s", protocol, domain, port, hash);

        log.info("Short URL created: {}", shortUrl);
        return shortUrl;
    }
}