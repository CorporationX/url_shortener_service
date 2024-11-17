package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.exceptions.InternalServerException;
import faang.school.urlshortenerservice.exception.exceptions.NotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String createShortUrl(String originalUrl) {
        String hash = hashCache.getHash();
        if (hash == null) {
            throw new InternalServerException("Failed to retrieve a hash for URL shortening");
        }

        Url urlEntity = new Url(hash, originalUrl, LocalDateTime.now());
        urlRepository.save(urlEntity);
        urlCacheRepository.save(hash, originalUrl);

        log.info("Short URL created: {} -> {}", hash, originalUrl);
        return hash;
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        String url = urlCacheRepository.find(hash);
        if (url != null) {
            return url;
        }

        Url urlEntity = urlRepository.findByHash(hash);
        if (urlEntity == null) {
            throw new NotFoundException("URL not found for hash: " + hash);
        }
        urlCacheRepository.save(hash, urlEntity.getUrl());
        return urlEntity.getUrl();
    }
}