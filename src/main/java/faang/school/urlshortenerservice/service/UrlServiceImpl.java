package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.CacheProperties;
import faang.school.urlshortenerservice.config.ClearProperties;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.CacheService;
import faang.school.urlshortenerservice.service.cache.HashCacheService;
import faang.school.urlshortenerservice.service.outbox.OutboxService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final OutboxService outboxService;
    private final CacheService<String> cacheService;
    private final HashCacheService hashCacheService;
    private final CacheProperties cacheProperties;
    private final ClearProperties clearProperties;
    private final UrlMapper urlMapper;
    private final EntityManager entityManager;

    @Override
    public String getUrl(String hash) {
        String counterKey = hash + "::counter";
        long counter = cacheService.incrementAndGet(counterKey);

        if (counter >= cacheProperties.getRequestThreshold()) {
            Duration ttl = Duration.ofMillis(cacheProperties.getTtlIncrementTimeMs());
            String url = cacheService.getValue(hash, String.class)
                    .map(cachedUrl -> {
                        cacheService.addExpire(hash, ttl);
                        return cachedUrl;
                    }).orElseGet(() -> {
                        String fetchedUrl = getUrlFromDatabaseBy(hash);
                        cacheService.put(hash, fetchedUrl, ttl);
                        return fetchedUrl;
                    });
            cacheService.delete(counterKey);
            return url;
        }

        return getUrlBy(hash);
    }

    @Override
    public String getUrlBy(String hash) {
        return cacheService.getValue(hash, String.class)
                .orElseGet(() -> getUrlFromDatabaseBy(hash));
    }

    @Override
    public String getUrlFromDatabaseBy(String hash) {
        return urlRepository.findById(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new EntityNotFoundException("Url with hash %s not found".formatted(hash)));
    }

    @Override
    @Transactional
    public String generateHashForUrl(UrlDto urlDto) {
        String url = urlDto.getUrl();
        if (urlRepository.existsByUrl(url)) {
            return url;
        }

        String freeHash = hashCacheService.getHash();
        Url entityUrl = urlMapper.toEntity(urlDto, freeHash);

        entityManager.persist(entityUrl);
        outboxService.saveOutbox(entityUrl);

        return url;
    }

    @Override
    public void clearOutdatedUrls() {
        List<String> releasedHashes = releaseOutdatedUrls();
        while (!releasedHashes.isEmpty()) {
            releasedHashes = releaseOutdatedUrls();
        }
    }

    private List<String> releaseOutdatedUrls() {
        List<String> releasedHashes = urlRepository.deleteOutdatedUrls(
                LocalDateTime.now().minusDays(clearProperties.getDaysThreshold()),
                clearProperties.getBatchSize()
        );
        hashCacheService.addHash(releasedHashes);
        return releasedHashes;
    }
}
