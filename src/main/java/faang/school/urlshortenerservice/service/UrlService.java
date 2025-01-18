package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.UrlUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlUtil urlUtil;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;

    @Value("${scheduler.clean-old-urls.ttl-months}")
    private int oldUrlsTtlMonths;

    @Transactional
    public String createShortUrl(UrlDto urlDto) {
        log.info("Creating short URL for long URL: {}", urlDto.getUrl());
        validateUrl(urlDto.getUrl());

        String freeHash = hashCache.getHash();
        urlRepository.save(createUrlEntity(urlDto.getUrl(), freeHash));
        String shortUrl = urlUtil.buildShortUrlFromContext(freeHash);
        urlCacheRepository.saveDefaultUrl(freeHash, urlDto.getUrl());

        log.info("Short URL={} for original URL={} was created!", shortUrl, urlDto.getUrl());
        return shortUrl;
    }

    @Cacheable(value = "shortUrls", key = "#hash", cacheManager = "urlHashCacheManager")
    public String getOriginalUrl(String hash) {
        return urlRepository.findOriginalUrlByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("Original URL not found for hash: %s".formatted(hash)));
    }

    @Transactional
    public void deleteOldUrls() {
        log.info("Starting removing old urls and moving their hashes to free hashes");
        LocalDateTime oldHashesThresholdDate = LocalDateTime.now().minusMonths(oldUrlsTtlMonths);
        List<String> oldHashes = urlRepository.deleteOldUrls(oldHashesThresholdDate);
        hashRepository.save(oldHashes);
        log.info("Finished removing old hashes and moving their hashes to free hashes: {}", oldHashes);
    }

    public List<Url> findUrlEntities(Set<String> urlHashes) {
        return urlRepository.findByHashes(urlHashes);
    }

    private void validateUrl(String url) {
        String urlWithProtocol = urlUtil.ensureUrlHasProtocol(url);
        if (!urlUtil.isValidUrl(urlWithProtocol)) {
            throw new DataValidationException("Invalid url!");
        }
    }

    private Url createUrlEntity(String url, String hash) {
        return Url.builder()
                .url(url)
                .hash(hash)
                .build();
    }
}
