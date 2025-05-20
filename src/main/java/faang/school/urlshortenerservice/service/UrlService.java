package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.chache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

@Service
@Slf4j
public class UrlService {

    private final HashCache cache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository cacheRepository;
    private final String shortUrl;

    public UrlService(HashCache cache,
                      UrlRepository urlRepository,
                      UrlCacheRepository cacheRepository,
                      @Value("${app.shortener.url}") String shortUrl) {
        this.cache = cache;
        this.urlRepository = urlRepository;
        this.cacheRepository = cacheRepository;
        this.shortUrl = shortUrl;
    }

    @Transactional
    public String getShortUrlLink(String originalUrl) {
        if (!validateUrl(originalUrl)) {
            throw  new InvalidUrlException("Введен несуществующий адрес: %s", originalUrl);
        }
        String hash = String.valueOf(cache.getHash().join());
        urlRepository.save(new Url(hash, originalUrl, LocalDateTime.now()));
        log.info("Сохранил в репозиторий");
        cacheRepository.save(hash, originalUrl);
        log.info("Сохранил в кеш");
        log.debug("Ссылка {} успешно ассоциирована с хешем {}", originalUrl, hash);
        return  shortUrl + hash;
    }

    public String getOriginalUrl(String hash) {
        if (hash == null || hash.isBlank()) {
            log.error("Хеш не может быть пуст");
            throw new DataValidationException("Ошибка валидации данных");
        }
        String urlFirstAttempt = cacheRepository.findByHash(hash);
        if (urlFirstAttempt != null) {
            log.debug("Url для хеша {} найден в кеше", hash);
            return urlFirstAttempt;
        }
        log.debug("Хеш {} отсутствует в кеше, иду в репозиторий", hash);

        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> new UrlNotFoundException("Адрес для хеша %s не найден", hash));
        log.debug("Url для хеша {} найден в репозитории", hash);
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
