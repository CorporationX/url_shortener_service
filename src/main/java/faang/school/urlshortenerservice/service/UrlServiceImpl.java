package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.CacheOperationException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.exception.UrlShorteningException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${hash.cache.ttl-in-seconds:172800}") //172800 два дня в секундах
    private long ttlInSeconds;

    @Value("${app.base-url}")
    private String baseUrlHttps;

    @Override
    @Transactional
    public ResponseUrlDto shorten(RequestUrlDto requestUrlDto) {
        UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
        if (!urlValidator.isValid(requestUrlDto.getUrl())) {
            throw new IllegalArgumentException("Некорректный формат URL: " + requestUrlDto.getUrl());
        }
        try {
            String hash = hashCache.getHash();

            Url url = new Url();
            url.setUrl(requestUrlDto.getUrl());
            url.setHash(hash);
            Url savedUrl = urlRepository.save(url);

            urlCacheRepository.save(hash, requestUrlDto.getUrl(), ttlInSeconds);

            return ResponseUrlDto.builder()
                    .originalUrl(savedUrl.getUrl())
                    .shortUrl(baseUrlHttps + savedUrl.getHash())
                    .build();
        } catch (Exception e) {
            log.error("Error while shortening URL: {}", requestUrlDto.getUrl(), e);
            throw new UrlShorteningException("Failed to shorten URL", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseUrlDto getOriginalUrl(String hash) {
        try {
            String cachedUrl = urlCacheRepository.get(hash);
            if (cachedUrl != null) {
                log.debug("Cache hit for hash: {}", hash);
                return ResponseUrlDto.builder()
                        .originalUrl(cachedUrl)
                        .build();
            }
        } catch (Exception e) {
            log.warn("Cache access failed for hash: {}. Falling back to database", hash, e);
        }

        try {
            String databaseUrl = urlRepository.findById(hash)
                    .map(Url::getUrl)
                    .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash));

            try {
                urlCacheRepository.save(hash, databaseUrl, ttlInSeconds);
            } catch (Exception e) {
                log.warn("Failed to save URL to cache for hash: {}", hash, e);
            }

            return ResponseUrlDto.builder()
                    .originalUrl(databaseUrl)
                    .build();
        } catch (DataAccessException e) {
            log.error("Database error while retrieving URL for hash: {}", hash, e);
            throw new CacheOperationException("Failed to retrieve URL from database", e);
        }
    }
} 