package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Setter
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Value("${url.short-url-suffix}")
    private String shortUrlSuffix;

    @Value("${url.short-url-ttl-in-seconds}")
    private long shortUrlTtlInSeconds;

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        String url = urlCacheRepository.getUrl(hash);
        if (url != null) {
            log.debug("Found URL in Redis for hash: {}", hash);
            return url;
        }

        Url urlEntity = urlRepository.findByHash(hash)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash));
        url = urlEntity.getUrl();
        urlCacheRepository.setUrl(hash, url);
        log.debug("Found URL in DB for hash: {}", hash);
        return url;
    }

    @Transactional
    public String createShortUrl(UrlDto urlDto) {
        String existingHash = urlRepository.findHashByUrl(urlDto.getUrl());
        if (existingHash != null) {
            String shortUrl = shortUrlSuffix + "/" + existingHash;
            log.debug("Returning existing short URL: {} for URL: {}", shortUrl, urlDto.getUrl());
            return shortUrl;
        }

        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .createdAt(LocalDateTime.now())
                .build();
        urlRepository.save(url);
        urlCacheRepository.setUrl(hash, urlDto.getUrl());
        String shortUrl = shortUrlSuffix + "/" + hash;
        log.info("Created short URL: {} for URL: {}", shortUrl, urlDto.getUrl());
        return shortUrl;
    }
}