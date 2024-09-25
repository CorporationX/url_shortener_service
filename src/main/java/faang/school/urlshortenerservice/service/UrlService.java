package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Transactional(readOnly = true)
    public String getUrlByHash(String hash) {
        log.info("Fetching URL for hash: {}", hash);
        return urlCacheRepository.findByHash(hash)
                .or(() -> {
                    log.info("Cache miss for hash: {}. Querying database.", hash);
                    return urlRepository.findUrlByHash(hash)
                            .map(url -> {
                                urlCacheRepository.save(hash, url);
                                log.info("URL found in database and saved to cache for hash: {}", hash);
                                return url;
                            });
                })
                .orElseThrow(() -> {
                    log.error("URL not found for hash: {}", hash);
                    return new EntityNotFoundException("URL not found for hash: " + hash);
                });
    }

    @Transactional
    public String createShortUrl(String url) {
        log.info("Creating short URL for: {}", url);
        String hash = hashCache.getHash();
        Url urlEntity = Url.builder()
                .hash(hash)
                .url(url)
                .build();
        urlRepository.save(urlEntity);
        urlCacheRepository.save(hash, url);
        log.info("Short URL created with hash: {} for original URL: {}", hash, url);
        return hash;
    }
}