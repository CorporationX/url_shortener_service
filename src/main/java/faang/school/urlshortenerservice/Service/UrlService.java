package faang.school.urlshortenerservice.Service;

import faang.school.urlshortenerservice.ExceptionHandler.UrlNotFoundException;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String createShortUrl(String originalUrl) {
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .build();
        urlRepository.save(url);
        log.info("Create short url : {}", url);
        urlCacheRepository.save(hash, originalUrl);
        return hash;
    }

    public String getOriginalUrl(String hash) {
        Optional<String> cachedUrl = urlCacheRepository.findOriginalUrl(hash);
        if (cachedUrl.isPresent()) {
            log.debug("Was found in Redis: {}", hash);
            return cachedUrl.get();
        }
        return urlRepository.findByHash(hash)
                .map(url -> {
                    log.debug("Найден в БД: {}", hash);
                    // кэшируем обратно
                    urlCacheRepository.save(hash, url.getUrl());
                    return url.getUrl();
                })
                .orElseThrow(() -> new UrlNotFoundException("can`t found url with hash: " + hash));
    }
}
