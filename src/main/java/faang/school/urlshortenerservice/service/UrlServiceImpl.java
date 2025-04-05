package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${short.url.domain:http://localhost:8080/}")
    private String shortUrlBase;

    @Value("${url.expiration.years:1}")
    private int expirationYears;

    @Override
    @Transactional
    public String createShortUrl(UrlRequestDto urlRequest) {
        log.info("Creating short URL for original URL: {}", urlRequest.getOriginalUrl());
        String longUrl = urlRequest.getOriginalUrl();
        String hash = hashCache.getHash();
        urlRepository.saveUrl(hash, longUrl);
        urlCacheRepository.saveUrl(hash, longUrl);
        String shortUrl = shortUrlBase + hash;
        log.info("Short URL created: {}", shortUrl);
        return shortUrl;
    }

    @Override
    public String getLongUrl(String hash) {
        log.info("Retrieving long URL for hash: {}", hash);
        return urlCacheRepository.findByHash(hash)
                .map(url -> {
                    log.debug("Found URL in cache: {}", url);
                    return url;
                })
                .orElseGet(() -> urlRepository.findByHash(hash)
                        .map(longUrl -> {
                            log.debug("Found URL in DB, saving to cache: {}", longUrl);
                            urlCacheRepository.saveUrl(hash, longUrl);
                            return longUrl;
                        })
                        .orElseThrow(() -> {
                            log.error("No URL found for hash: {}", hash);
                            return new UrlNotFoundException("No URL found for hash: " + hash);
                        }));
    }
}