package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.chache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashCache cache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository cacheRepository;

    @Transactional
    public String getShortUrlLink(String originalUrl) {
        if (!validateUrl(originalUrl)) {
            throw  new InvalidUrlException("Введен несуществующий адрес: %s", originalUrl);
        }

        String hash = String.valueOf(cache.getHash());
        urlRepository.save(new Url(hash, originalUrl, LocalDateTime.now()));
        cacheRepository.save(hash, originalUrl);
        log.debug("Ссылка {} успешно ассоциирована с хешем {}", originalUrl, hash);
        return  String.join(originalUrl + hash);
    }

    public String getOriginalUrl(String hash) {
        String urlFirstAttempt = cacheRepository.findByHash(hash);
        if (urlFirstAttempt != null) {
            log.debug("Url для хеша {} не найден в кеше", hash);
            return urlFirstAttempt;
        }
        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> new UrlNotFoundException("Адрес для хеша %d не найден", hash));
        return url.getUrl();

    }

    private boolean validateUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isBlank()) {
            log.error("Получено недопустимое значение {}", originalUrl);
            throw new InvalidUrlException("Введен несуществующий адрес: %s", originalUrl);
        }
        try {
            URL url = new URL(originalUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            log.error("Предоставлен несуществующий URL: {}", originalUrl);
            return  false;
        }
    }

}
