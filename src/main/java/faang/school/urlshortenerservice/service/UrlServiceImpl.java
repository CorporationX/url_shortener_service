package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.exception.CacheRefillException;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static faang.school.urlshortenerservice.exception.ErrorMessages.HASH_COLLISION_MESSAGE;
import static faang.school.urlshortenerservice.exception.ErrorMessages.HASH_GENERATION_ATTEMPTS_FAILED;
import static faang.school.urlshortenerservice.exception.ErrorMessages.INVALID_SCHEME_MESSAGE;
import static faang.school.urlshortenerservice.exception.ErrorMessages.INVALID_URL_TEMPLATE;
import static faang.school.urlshortenerservice.exception.ErrorMessages.URL_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    private static final int MAX_HASH_GENERATION_ATTEMPTS = 3;

    @Value("${shortener.domain}")
    private String domain;

    @Override
    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        return urlCacheRepository.findByHash(hash).or(() -> {
                    Optional<String> originalUrl = urlRepository.findByHash(hash);
                    originalUrl.ifPresent(url -> urlCacheRepository.save(hash, url));
                    return originalUrl;
                })
                .orElseThrow(() -> new UrlNotFoundException(URL_NOT_FOUND));
    }

    @Override
    public String createShortUrl(String originalUrl) {
        validateUrl(originalUrl);
        String hash = getUniqueHash();
        saveUrlMapping(hash, originalUrl);
        return buildShortUrl(hash);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void saveUrlMapping(String hash, String originalUrl) {
        try {
            urlRepository.save(hash, originalUrl);
            urlCacheRepository.save(hash, originalUrl);
        } catch (DuplicateKeyException e) {
            log.error("Hash collision occurred: {}", hash);
            throw new CacheRefillException(String.format(HASH_COLLISION_MESSAGE, hash));
        }

    }

    private String buildShortUrl(String hash) {
        return String.format("%s/%s", domain, hash);
    }

    private void validateUrl(String url) {
        try {
            new URI(url).parseServerAuthority();
        } catch (URISyntaxException | IllegalArgumentException e) {
            throw new InvalidUrlException(String.format(INVALID_URL_TEMPLATE, url));
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new InvalidUrlException(INVALID_SCHEME_MESSAGE);
        }
    }

    private String getUniqueHash() {
        int attempts = 0;
        while (attempts < MAX_HASH_GENERATION_ATTEMPTS) {
            String hash = hashCache.getHash();
            if (!urlRepository.existsByHash(hash)) {
                return hash;
            }
            log.warn("Hash collision detected: {}", hash);
            attempts++;
        }
        throw new CacheRefillException(String.format(HASH_GENERATION_ATTEMPTS_FAILED, attempts));
    }
}

