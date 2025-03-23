package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cashe.HashCache;
import faang.school.urlshortenerservice.cashe.ShortUrlCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlShorteningException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final ShortUrlCache shortUrlCache;
    private final HashCache hashCache;

    @Value("${server.url}")
    private String domain;

    public String createShortUrl(String longUrl) {
        log.info("Получен запрос на создание короткого URL-адреса: {}", longUrl);

        String existingHash = urlRepository.hashForUrlIfExists(longUrl);
        if (existingHash != null) {
            String existingShortUrl = domain + "/" + existingHash;
            log.info("Коороткий URL для {} уже существует: {}", longUrl, existingShortUrl);
            return existingShortUrl;
        }

        String hash = hashCache.getHash();
        Url url = new Url(hash, longUrl, Timestamp.valueOf(LocalDateTime.now()));

        try {
            urlRepository.save(url);
        } catch (ConstraintViolationException e) {
            log.warn("Нарушение уникальности при сохранении URL {}: {}", longUrl, e.getMessage());
            existingHash = urlRepository.hashForUrlIfExists(longUrl);
            if (existingHash != null) {
                String existingShortUrl = domain + "/" + existingHash;
                log.info("Короткая ссылка для {} уже существует: {}", longUrl, existingShortUrl);
                return existingShortUrl;
            }
            throw new UrlShorteningException("Ошибка при сохранении URL: " + longUrl, e);
        }

        shortUrlCache.saveUrl(hash, longUrl);
        return domain + "/" + hash;
    }
}

