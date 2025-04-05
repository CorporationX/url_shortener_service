package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exeption.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    @Value("${spring.base_url}")
    private String baseUrl;

    @Value("${spring.url.expiration-period}")
    private Duration expirationPeriod;

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String createShortUrl(String url) {
        String hash = hashCache.getHash();

        Url urlEntity = Url.builder()
                .hash(hash)
                .url(url)
                .deletedAt(LocalDateTime.now().plus(expirationPeriod))
                .build();

        urlRepository.save(urlEntity);
        log.info("Created new shortUrl: " + url);

        urlCacheRepository.set(hash, url);

        return buildShortUrl(hash);
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        Optional<String> originalUrl = urlCacheRepository.get(hash);
        if (originalUrl.isPresent()) {
            return originalUrl.get();
        }

        String url = urlRepository
                .findById(hash)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash))
                .getUrl();

        urlCacheRepository.set(hash, url);

        return url;
    }

    private String buildShortUrl(String hash) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(hash)
                .build()
                .toUriString();
    }
}
