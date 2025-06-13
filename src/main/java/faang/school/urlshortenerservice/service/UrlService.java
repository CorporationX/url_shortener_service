package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.UrlProperties;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlCacheRepository cacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlProperties properties;

    public String createShortUrl(String originalUrl) {
        String hash = hashCache.getHash();

        Url url = new Url(hash, originalUrl);
        urlRepository.save(url);
        cacheRepository.save(hash, originalUrl);

        log.debug("Создана короткая ссылка: {} → {}", hash, originalUrl);
        return hash;
    }

    public String getOriginalUrl(String hash) {
        return cacheRepository.findByHash(hash)
                .orElseGet(() -> {
                    log.debug("Ссылка не найдена в Redis по хэшу: {}", hash);

                    return urlRepository.findById(hash)
                            .map(url -> {
                                String originalUrl = url.getUrl();
                                cacheRepository.save(hash, originalUrl);
                                log.info("Ссылка получена из БД и сохранена в Redis для хэша: {}", hash);
                                return originalUrl;
                            })
                            .orElseThrow(() -> {
                                log.warn("Ссылка не найдена ни в Redis, ни в БД по хэшу: {}", hash);
                                return new UrlNotFoundException(hash);
                            });
                });
    }

    public String getDomain() {
        return properties.getDomain();
    }
}