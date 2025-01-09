package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.hash.util.HashCache;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    /**
     * Из настроек или application.yaml можно подтянуть базовый адрес сокращалки
     * (например, https://sh.com), чтобы сформировать итоговую короткую ссылку.
     */
    @Value("${app.shortener.base-url:https://sh.com}")
    private String baseShortUrl;

    /**
     * TTL (в часах) для хранения в Redis.
     * Допустим, в application.yaml: spring.date.ttl.hour.url = 24
     */
    @Value("${spring.date.ttl.hour.url:24}")
    private long urlTtlInCache;

    /**
     * Создаёт запись (hash -> originalUrl) в БД и Redis, возвращая короткий URL.
     *
     * @param originalUrl исходная длинная ссылка, которую нужно сократить
     * @return короткий URL вида "https://sh.com/{hash}" или другой
     */
    @Transactional
    public String createHashUrl(String originalUrl) {
        // 1. Получаем свободный хэш из локального кэша
        String hash = hashCache.getHash();

        // 2. Создаём entity "Url"
        Url urlEntity = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .build();

        // 3. Сохраняем в БД
        urlRepository.save(urlEntity);

        // 4. Сохраняем в Redis (быстрый доступ)
        urlCacheRepository.saveByTtlInHour(urlEntity, urlTtlInCache);

        // 5. Формируем короткую ссылку, добавив hash к базовому адресу
        //    Например, если baseShortUrl = https://sh.com, итог будет https://sh.com/{hash}
        return baseShortUrl + "/" + hash;
    }

    @Transactional(readOnly = true)
    public String getPrimalUri(String hash) {
        // 1) check Redis first
        Optional<Url> cachedUrl = urlCacheRepository.findByHash(hash);
        if (cachedUrl.isPresent()) {
            return cachedUrl.get().getUrl();
        }

        // 2) if not in Redis, check DB
        Url found = urlRepository.findById(hash)
                .orElseThrow(() -> new ResourceNotFoundException("Url not found for hash: %s", hash));

        // 3) (optionally) refresh in Redis
        urlCacheRepository.saveByTtlInHour(found, urlTtlInCache);

        // 4) return original link
        return found.getUrl();
    }
}
