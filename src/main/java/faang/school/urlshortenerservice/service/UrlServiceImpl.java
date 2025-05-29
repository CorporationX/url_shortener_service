package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EmptyHashCacheException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.service.url",
        havingValue = "default",
        matchIfMissing = true
)
public class UrlServiceImpl implements UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${url.cache-ttl:24h}")
    private Duration cacheTtl;

    @Value("${url.default-url}")
    private String defaultUrl;

    @Override
    @Transactional
    public UrlResponseDto createShortUrl(UrlRequestDto urlRequestDto) {
        String originalUrl = urlRequestDto.getOriginalUrl();
        log.info("Starting create short URL for {}", originalUrl);

        Optional<Url> existingUrl = urlRepository.findByUrl(originalUrl);
        if (existingUrl.isPresent()) {
            log.info("Short URL has been exist for {}", originalUrl);
            return new UrlResponseDto(getShortUrl(existingUrl.get().getHash()));
        }

        String hash = hashCache.getHash()
                .orElseThrow(() -> new EmptyHashCacheException("Hash cache is empty"));

        Url url = new Url();
        url.setHash(hash);
        url.setUrl(originalUrl);

        urlRepository.save(url);
        urlCacheRepository.saveUrl(hash, originalUrl, cacheTtl);
        log.info("The short URL for {} has been successfully created", originalUrl);

        return new UrlResponseDto(getShortUrl(hash));
    }

    @Override
    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        log.info("Starting get original URL by hash {}...", hash);
        return urlCacheRepository.findUrlAndExpire(hash, cacheTtl)
                .or(() -> urlRepository.findByHash(hash)
                        .map(urlEntity -> {
                            String originalUrl = urlEntity.getUrl();
                            urlCacheRepository.saveUrl(hash, originalUrl, cacheTtl);
                            return originalUrl;
                        }))
                .orElseThrow(() -> new UrlNotFoundException("URL not found by hash: " + hash));
    }

    private String getShortUrl(String hash) {
        return defaultUrl + hash;
    }
}
