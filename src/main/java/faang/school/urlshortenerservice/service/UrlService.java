package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.ShortUrlResponse;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${url.shortener.base-url:https://short.com/}")
    private String baseUrl;

    /**
     * Создаёт короткую ссылку для длинного URL
     *
     * @param longUrl длинный URL
     * @return объект с короткой ссылкой и хэшем
     */
    @Transactional
    public ShortUrlResponse createShortUrl(String longUrl) {
        log.info("Creating short URL for: {}", longUrl);

        String hash = hashCache.getHash();
        log.debug("Retrieved hash from cache: {}", hash);

        Url url = Url.builder()
                .hash(hash)
                .url(longUrl)
                .createdAt(LocalDateTime.now())
                .build();

        urlRepository.save(url);
        log.debug("Saved URL to database: hash={}", hash);

        urlCacheRepository.save(hash, longUrl);
        log.debug("Saved URL to Redis cache: hash={}", hash);

        String shortUrl = baseUrl + hash;
        log.info("Successfully created short URL: {}", shortUrl);

        return ShortUrlResponse.builder()
                .shortUrl(shortUrl)
                .hash(hash)
                .build();
    }

    /**
     * Получает оригинальный URL по хэшу
     * Сначала проверяет Redis, затем БД
     *
     * @param hash хэш короткой ссылки
     * @return оригинальный URL
     * @throws UrlNotFoundException если URL не найден
     */
    public String getOriginalUrl(String hash) {
        log.debug("Getting original URL for hash: {}", hash);

        String url = urlCacheRepository.get(hash);
        if (url != null) {
            log.debug("Found URL in Redis cache: hash={}", hash);
            return url;
        }

        log.debug("URL not found in cache, checking database: hash={}", hash);
        Url urlEntity = urlRepository.findById(hash)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash));

        urlCacheRepository.save(hash, urlEntity.getUrl());

        return urlEntity.getUrl();
    }
}