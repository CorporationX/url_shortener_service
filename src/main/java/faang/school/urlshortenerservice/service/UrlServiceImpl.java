package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.LocalCache;
import faang.school.urlshortenerservice.service.cache.UrlRetrieverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {
    private final LocalCache localCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRetrieverService urlRetrieverService;

    @Value("${redis.cache.ttl.hash-hours}")
    private long cacheTtlHours;

    @Override
    @Transactional
    public String getShortUrl(String longUrl) {
        try {
            String hash = localCache.getHash();
            log.info("Retrieved hash '{}' from local cache.", hash);
            Url url = Url.builder()
                    .hash(hash)
                    .url(longUrl)
                    .build();
            urlRepository.save(url);
            log.info("Successfully saved URL entity for hash '{}' to the database.", hash);
            urlCacheRepository.save(hash, longUrl, Duration.ofHours(cacheTtlHours));
            log.info("Successfully cached mapping for hash '{}' in Redis with a TTL of {} hour(s).",
                    hash, cacheTtlHours);
            return hash;
        } catch (Exception e) {
            log.error("Failed to create short URL for: {}", longUrl, e);
            throw new RuntimeException("Failed to generate short URL. Please try again later.", e);
        }
    }

    @Override
    public String getLongUrl(String hash) {
        return urlRetrieverService.getLongUrl(hash)
                .orElseThrow(() -> new UrlNotFoundException(hash));
    }
}
