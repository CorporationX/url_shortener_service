package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private static final Logger logger = LoggerFactory.getLogger(UrlServiceImpl.class);

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
        logger.info("Creating short URL for original URL: {}", urlRequest.getOriginalUrl());
        String longUrl = urlRequest.getOriginalUrl();
        String hash = hashCache.getHash();
        urlRepository.saveUrl(hash, longUrl);
        urlCacheRepository.saveUrl(hash, longUrl);
        String shortUrl = shortUrlBase + hash;
        logger.info("Short URL created: {}", shortUrl);
        return shortUrl;
    }

    @Override
    public String getLongUrl(String hash) {
        logger.info("Retrieving long URL for hash: {}", hash);
        return Optional.ofNullable(urlCacheRepository.findByHash(hash))
                .map(url -> {
                    logger.debug("Found URL in cache: {}", url);
                    return url;
                })
                .or(() -> Optional.ofNullable(urlRepository.findByHash(hash))
                        .map(longUrl -> {
                            logger.debug("Found URL in DB, saving to cache: {}", longUrl);
                            urlCacheRepository.saveUrl(hash, longUrl);
                            return longUrl;
                        }))
                .orElseThrow(() -> {
                    logger.error("No URL found for hash: {}", hash);
                    return new UrlNotFoundException("No URL found for hash: " + hash);
                });
    }

    public void deleteExpiredUrls() {
        logger.info("Starting deletion of expired URLs");
        LocalDateTime expirationDate = LocalDateTime.now().minusYears(expirationYears);
        urlRepository.deleteExpiredUrls(expirationDate);
        logger.info("Expired URLs deleted up to: {}", expirationDate);
    }
}