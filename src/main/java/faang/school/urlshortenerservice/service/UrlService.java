package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cashe.HashCache;
import faang.school.urlshortenerservice.cashe.ShortUrlCache;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.exception.UrlShorteningException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashRepository hashRepository;
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

    public String getOriginalUrl(String hash) {
        String cachedUrl = shortUrlCache.getUrl(hash);
        if (cachedUrl != null) {
            return cachedUrl;
        }
        return urlRepository.findById(hash)
                .map(Url::getUrl)
                .orElseThrow(() ->
                        new UrlNotFoundException(hash));
    }

    @Transactional
    public void cleanUpExpiredUrls() {
        log.info("Начало очистки устаревших URL-адресов...");
        List<Hash> availableHashes = urlRepository.removeExpiredUrls()
                .stream().map(Hash::new)
                .toList();
        hashRepository.saveAll(availableHashes);
        log.info("Очистка завершена. Количество обработанных хэшей: {}", availableHashes.size());
    }
}