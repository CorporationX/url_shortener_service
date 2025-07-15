package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCacheService hashCacheService;
    private final UrlRepository urlRepository;
    private final UrlCacheService urlCacheService;

    @Value("${app.base-url}")
    private String baseUrl;

    public String getUrlByHash(String hash) {
        Url urlFromCache = urlCacheService.getUrl(hash);
        if (urlFromCache != null) {
            return urlFromCache.getUrl();
        }

        Url urlFromDb = urlRepository.findByHash(hash);
        if (urlFromDb != null) {
            urlCacheService.saveUrl(hash, urlFromDb);
            return urlFromDb.getUrl();
        }
        return new Exception("No URL found for hash: " + hash).getMessage();
    }

    public String createShortUrl(String url) {
        log.info("Getting Hash for URL: {}", url);
        String hash = hashCacheService.getHash();
        log.info("Hash for URL: {} is: {}", url, hash);

        Url newUrl = newUrl(url, hash);

        log.info("Saving URL with Hash to DB for: {}", url);
        urlRepository.save(newUrl);

        log.info("Saving URL with Hash to Redis for: {}", url);
        urlCacheService.saveUrl(hash, newUrl);

        return baseUrl + "/" + hash;
    }

    private Url newUrl(String url, String hash) {
        return Url.builder()
                .url(url)
                .hash(hash)
                .build();
    }
}
