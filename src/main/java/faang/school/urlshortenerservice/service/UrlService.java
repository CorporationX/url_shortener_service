package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
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

/**
 * Сервис для работы с короткими URL.
 * <p>
 * Отвечает за создание коротких ссылок и получение оригинального URL по хэшу.
 * Использует кэш и базу данных для хранения и ускоренного доступа.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    @Value("${url.short-url-suffix}")
    private String shortUrlSuffix;
    @Value("${url.short-url-ttl-in-seconds}")
    private long shortUrlTtlInSeconds;

    /**
     * Получает оригинальный URL по хэшу.
     * <p>
     * Сначала ищет URL в кэше, если не находит — в базе данных. При нахождении в БД записывает в кэш.
     *
     * @param hash хэш короткого URL
     * @return оригинальный URL
     * @throws UrlNotFoundException если URL по хэшу не найден
     */
    @Transactional
    public String getUrl(String hash) {
        String urlFromCache = urlCacheRepository.getUrl(hash);
        if (urlFromCache == null) {
            log.debug("URL for hash {} not found", hash);
            Url urlFromRepository = urlRepository.findByHash(hash)
                    .orElseThrow(() -> new UrlNotFoundException("For hash " + hash + " url not found"));

            String url = urlFromRepository.getUrl();
            urlCacheRepository.setUrl(hash, url);

            log.debug("URL for hash {} found in db", url);

            return url;
        }

        log.debug("Found URL {} for hash {}", urlFromCache, hash);

        return urlFromCache;
    }

    /**
     * Создаёт короткий URL для переданного {@link UrlDto}.
     * <p>
     * Получает хэш из кэша, сохраняет URL в базу и кэш, возвращает сформированную короткую ссылку.
     *
     * @param urlDto DTO с оригинальным URL
     * @return короткий URL (например, <a href="http://localhost:8080/abc123">...</a>)
     */
    @Transactional
    public String createShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        Url url = toUrl(urlDto, hash);
        urlRepository.save(url);
        urlCacheRepository.setUrl(hash, urlDto.getUrl());

        String shortUrl = shortUrlSuffix + "/" + hash;
        log.debug("Hash {} created for url {}", hash, shortUrl);
        return shortUrl;
    }

    private Url toUrl(UrlDto urlDto, String hash) {
        LocalDateTime now = LocalDateTime.now();
        return Url.builder()
                .url(urlDto.getUrl())
                .hash(hash)
                .createdAt(now)
                .deletedAt(now.plusSeconds(shortUrlTtlInSeconds))
                .build();
    }
}
