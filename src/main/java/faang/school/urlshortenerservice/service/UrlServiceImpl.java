package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {
    private final HashCache cache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Override
    @Transactional
    public String shorten(String originalUrl) {
        if(!isValidUrl(originalUrl)) {
            log.error("Invalid url: {}", originalUrl);
            throw new InvalidUrlException(String.format("Invalid URL: %s", originalUrl));
        }
        Optional<Url> url = urlRepository.findByUrl(originalUrl);
        if (url.isPresent()) {
            return url.get().getHash();
        }

        String hash = cache.getHash();
        Url newUrl = new Url(hash, originalUrl, LocalDateTime.now());
        urlRepository.save(newUrl);
        log.info("Saved short URL in database: hash='{}', original URL='{}'", hash, originalUrl);
        urlCacheRepository.save(hash, originalUrl);

        return hash;
    }

    @Override
    @Transactional
    public String getOriginal(String hash) {
        Optional<String> cachedUrl = urlCacheRepository.get(hash);
        if (cachedUrl.isPresent()) {
            return cachedUrl.get();
        }

        log.debug("Hash {} not found in redis . Searching in database...", hash);
        Url originalUrl = urlRepository.findById(hash).orElseThrow(() -> {
            log.error("Hash {} not found", hash);
            return new EntityNotFoundException(String.format("Hash %s not found", hash));
        });
        log.debug("Hash {} found in database .", hash);
        urlCacheRepository.save(hash, originalUrl.getUrl());
        return originalUrl.getUrl();
    }

    private boolean isValidUrl(String url) {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(url);
    }
}
