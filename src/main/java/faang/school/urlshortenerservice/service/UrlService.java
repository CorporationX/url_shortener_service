package faang.school.urlshortenerservice.service;

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

    public String getOriginalUrl(String hash) {
        return cacheRepository.findByHash(hash)
                .orElseGet(() -> {
                    log.info("Ссылка не найдена в Redis по хэшу: {}", hash);
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
}