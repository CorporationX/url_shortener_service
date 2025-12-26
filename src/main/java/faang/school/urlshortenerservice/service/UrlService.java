package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${url.shortener.base-url}")
    private String baseUrl;

    public String createShortUrl(String longUrl) {
        log.info("Creating short URL for: {}", longUrl);
        String hash = hashCache.getHash();
        log.debug("Retrieved hash from cache: {}", hash);

        urlRepository.save(hash, longUrl);
        urlCacheRepository.save(hash, longUrl);

        String shortUrl = String.format("%s/%s", baseUrl, hash);
        log.info("Successfully created short URL: {}", shortUrl);
        return shortUrl;
    }

    public String getOriginalUrl(String hash) {
        log.debug("Looking up URL for hash: {}", hash);

        return urlCacheRepository.findByHash(hash)
                .map(url -> {
                    log.debug("Cache hit for hash: {}", hash);
                    return url;
                })
                .orElseGet(() -> {
                    log.debug("Cache miss for hash: {}, checking database", hash);
                    String url = urlRepository.findByHash(hash)
                            .orElseThrow(() -> {
                                log.error("URL not found for hash: {}", hash);
                                return new UrlNotFoundException("URL not found for hash: " + hash);
                            });

                    log.debug("Caching URL for hash: {}", hash);
                    urlCacheRepository.save(hash, url);
                    return url;
                });
    }
}