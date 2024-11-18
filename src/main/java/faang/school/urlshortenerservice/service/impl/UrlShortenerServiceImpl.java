package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.model.entity.RedisCachedUrl;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.service.UrlShortenerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerServiceImpl implements UrlShortenerService {

    @Value("${domain-name: https://urlshrinker.com}")
    private String domainName;

    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;

    @Override
    @Transactional
    public String shrinkUrl(Url urlEntity) {
        log.info("Shrinking URL: {}", urlEntity.getLongUrl());

        String result = urlRepository.findByLongUrl(urlEntity.getLongUrl())
                .map(url -> {
                    String hashString = url.getHash();
                    log.info("URL found in database with hash: {}", hashString);

                    String longUrl = url.getLongUrl();
                    saveToRedisCache(hashString, longUrl);
                    return domainName + "/" + hashString;
                })
                .orElseGet(() -> {
                    String hashString = hashCache.getHash();
                    log.info("Generated new hash for URL: {}", hashString);

                    String longUrl = urlEntity.getLongUrl();
                    saveToRedisCache(hashString, longUrl);
                    saveToEntity(hashString, longUrl);
                    return domainName + "/" + hashString;
                });

        log.info("Shrank URL: {} to {}", urlEntity.getLongUrl(), result);
        return result;
    }

    private void saveToRedisCache(String hash, String longUrl) {
        log.debug("Saving URL to Redis cache with hash: {}", hash);
        RedisCachedUrl redisCachedUrl = new RedisCachedUrl();
        redisCachedUrl.setId(hash);
        redisCachedUrl.setLongUrl(longUrl);
        redisCachedUrl.setCreatedAt(LocalDateTime.now());
        urlCacheRepository.save(redisCachedUrl);
        log.debug("Saved URL to Redis cache successfully");
    }

    private void saveToEntity(String hash, String longUrl) {
        log.debug("Saving URL to database with hash: {}", hash);
        Url url = new Url();
        url.setHash(hash);
        url.setLongUrl(longUrl);
        url.setCreatedAt(LocalDateTime.now());
        urlRepository.save(url);
        log.debug("Saved URL to database successfully");
    }

    @Override
    public String getOriginalUrl(String shortenedUrl) {
        log.info("Retrieving original URL for shortened URL: {}", shortenedUrl);

        String result = urlCacheRepository.findById(shortenedUrl)
                .map(redisCachedUrl -> {
                    log.info("Found URL in Redis cache for hash: {}", shortenedUrl);
                    return redisCachedUrl.getLongUrl();
                })
                .orElseGet(() -> {
                    log.info("URL not found in Redis cache, searching in database for hash: {}", shortenedUrl);
                    return urlRepository.findByHash(shortenedUrl)
                            .map(url -> {
                                log.info("Found URL in database for hash: {}", shortenedUrl);

                                saveToRedisCache(shortenedUrl, url.getLongUrl());

                                return url.getLongUrl();
                            })
                            .orElseThrow(() -> {
                                log.error("URL not found for hash: {}", shortenedUrl);
                                return new EntityNotFoundException("The specified shortened URL does not exist. Please create a new shortened URL.");
                            });
                });

        log.info("Retrieved original URL: {}", result);
        return result;
    }
}
