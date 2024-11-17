package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlShortenerService {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;
    private final HashCache hashCache;

    @Transactional
    public void cleanOldUrls() {

//        // Получение URL-адресов старше года
//        Список<Url> oldUrls = urlRepository.findOldUrls(); // Реализуй этот метод в URLRepository
//
//// Сохрани освобожденные хэши обратно в хэш-таблицу
//        for (Url url : oldUrls) {
//            hashRepository.save(url.getHash()); // Настроить в соответствии с твоей логикой получения хэшей
//            urlRepository.delete(url); // Удали старую ассоциацию URL.
//        }
    }

    public String getUrl(String hash) {
        String cachedUrl = urlCacheRepository.getUrlByHash(hash);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        Url url = urlRepository.findUrlByHash(hash).orElseThrow(() -> {
            String message = String.format("Cannot find url by %s", hash);
            log.info(message);
            return new RuntimeException(message);
        });

        urlCacheRepository.save(url);
        return url.getUrl();
    }

    @Transactional
    public String createShortLink(Url url) {
        String cachedHash = urlCacheRepository.getHashByUrl(url.getUrl());
        if (cachedHash == null) {
            cachedHash = urlRepository.findHashByUrl(url.getUrl()).orElse(null);
        }

        if (cachedHash == null) {

            String newHash = hashCache.getHash();

            Url newUrl = Url.builder()
                    .url(url.getUrl())
                    .hash(newHash)
                    .build();

            Url savedUrl = urlRepository.save(newUrl);
            urlCacheRepository.save(savedUrl);

            return savedUrl.getHash();
        }

        return cachedHash;
    }
}
