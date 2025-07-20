package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCacheService;
import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCacheService hashCacheService;
    private final UrlRepository urlRepository;
    private final UrlCacheService urlCacheService;
    private final HashService hashService;

    @Value("${service.url-service.base-url}")
    private String baseUrl;

    @Value("${service.url-service.old-urls-days}")
    private int oldUrlsDays;

    public String getUrlByHash(String hash) {
        log.info("Getting URL by hash: {}", hash);
        Url urlFromCache = urlCacheService.getUrl(hash);
        if (urlFromCache != null) {
            log.info("URL found in cache for hash: {}", hash);
            return urlFromCache.getUrl();
        }

        log.info("URL not found in cache, checking database for hash: {}", hash);
        Url urlFromDb = urlRepository.findByHash(hash);
        if (urlFromDb != null) {
            log.info("URL found in database and started loading to cache for hash: {}", hash);
            urlCacheService.saveUrl(hash, urlFromDb);
            return urlFromDb.getUrl();
        }
        return new UrlNotFoundException(hash).getMessage();
    }

    @Transactional
    public String createShortUrl(UrlDto urlDto) {
        String url = urlDto.getUrl();

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

    @Transactional
    public void cleanUnusedHash() {
        log.info("Getting old hashes from the database");
        List<Url> oldUrls = urlRepository.getUrlsOlderMoreThanDays(oldUrlsDays);
        List<String> oldHashes = oldUrls.stream().map(Url::getHash).toList();

        log.info("Check used hashes in Redis");
        List<String> unusedHashes = urlCacheService.checkUnusedHashes(oldHashes);

        log.info("Cleaning unused hashes from the database");
        urlRepository.deleteUrlsByHashes(unusedHashes);

        log.info("Save cleaned hashes to database");
        hashService.saveCleanedHashesToDatabase(unusedHashes);
    }


    private Url newUrl(String url, String hash) {
        return Url.builder()
                .url(url)
                .hash(hash)
                .build();
    }
}
