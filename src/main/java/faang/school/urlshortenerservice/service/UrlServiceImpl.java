package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.dto.OriginalUrl;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {
    private final HashCache cache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Override
    @Transactional
    public String shorten(OriginalUrl originalUrl) {
        String originalUrlString = originalUrl.url();
        if (!isValidUrl(originalUrlString)) {
            log.error("Invalid url: {}", originalUrlString);
            throw new InvalidUrlException(String.format("Invalid URL: %s", originalUrl.url()));
        }

        Url url = urlRepository.findByUrl(originalUrlString);
        if (url != null) {
            return url.getHash();
        }
        String hash = cache.getHash();
        url = new Url(hash, originalUrlString, LocalDateTime.now());
        urlRepository.save(url);
        log.info("Saved short URL in database: hash='{}', original URL='{}'", hash, originalUrlString);
        urlCacheRepository.save(hash, originalUrl.url());

        return hash;
    }

    @Override
    @Transactional
    public String getOriginal(String hash) {
        String cachedUrl = urlCacheRepository.get(hash);
        if (cachedUrl == null) {
            log.info("Hash {} not found in redis . Searching in database...", hash);
            Url originalUrl = urlRepository.findById(hash).orElseThrow(() -> {
                log.error("Hash {} not found", hash);
                return new EntityNotFoundException(String.format("Hash %s not found", hash));
            });
            log.info("Hash {} found in database .", hash);
            urlCacheRepository.save(hash, originalUrl.getUrl());
            return originalUrl.getUrl();
        }
        return cachedUrl;
    }

    public boolean isValidUrl(String urlString) {
        try {
            URI uri = new URI(urlString);
            if (uri.getScheme() == null ||
                    (!uri.getScheme().equalsIgnoreCase("http") &&
                            !uri.getScheme().equalsIgnoreCase("https"))) {
                return false;
            }
            if (uri.getHost() == null) return false;
            if (uri.getHost().startsWith(".")) return false;

            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
