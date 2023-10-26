package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.exception.url.*;
import faang.school.urlshortenerservice.repository.UrlRepository;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    @Value("${url.shortener-service.address}")
    private String serverAddress;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String shortenUrl(String originalURL) {
        String hash = hashCache.getHash().getHash();

        saveUrlToRepository(hash, originalURL);
        saveUrlToCache(hash, originalURL);

        return serverAddress + hash;
    }

    private void saveUrlToRepository(String hash, String originalURL) {
        try {
            urlRepository.save(Url.builder()
                    .hash(hash)
                    .url(originalURL)
                    .build());
        } catch (DataAccessException e) {
            log.error("Error while saving URL in repository: {}", e.getMessage());
        }
    }

    private void saveUrlToCache(String hash, String originalURL) {
        try {
            urlCacheRepository.saveToCache(hash, originalURL);
        } catch (DataAccessException e) {
            log.error("Error while saving URL in cache: {}", e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String getOriginalURL(String shortURL) {

        String hash = extractHashFromURL(shortURL);

        String originalURL = urlCacheRepository.getFromCache(hash);

        if (originalURL == null) {
            Url url = urlRepository.findByHash(hash);
            if (url != null) {
                originalURL = url.getUrl();

                urlCacheRepository.saveToCache(hash, originalURL);
            }
        }

        if (originalURL != null) {
            return originalURL;
        } else {
            log.error("Original URL not found for hash: {}", hash);
            throw new UrlNotFoundException("Original URL not found");
        }
    }

    String extractHashFromURL(String shortURL) {
        if (shortURL == null || shortURL.isEmpty()) {
            log.error("Invalid short URL: null or empty");
            throw new IllegalArgumentException("Invalid short URL");
        }

        Pattern pattern = Pattern.compile(Pattern.quote(serverAddress) + "([A-Za-z0-9]{6})");
        Matcher matcher = pattern.matcher(shortURL);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Invalid short URL format");
        }
    }
}