package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.managers.HashCache;
import faang.school.urlshortenerservice.repozitory.HashRepository;
import faang.school.urlshortenerservice.repozitory.UrlRepository;
import faang.school.urlshortenerservice.repozitory.redis.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final String shortUrlTemplate = "http://faang.url/api/v1/url/";


    public String shortUrl(String longUrl) {
        log.info("Received request to create short URL for: {}", longUrl);
        String shortUrl = shortUrlInDb(longUrl);
        if (shortUrl != null) {
            log.info("Found existing short URL for {}: {}", longUrl, shortUrl);
            return shortUrl;
        }

        String hash = hashCache.getHash();
        Url url = new Url(hash, longUrl);

        try {
            urlRepository.save(url);
        } catch (ConstraintViolationException e) {
            log.warn("Constraint violation while saving URL {}: {}", longUrl, e.getMessage());
            shortUrl = shortUrlInDb(longUrl);
            if (shortUrl != null) {
                return shortUrl;
            }
        }
        urlCacheRepository.save(hash, longUrl);

        String finalShortUrl = shortUrlTemplate + hash;
        log.info("Short URL created for {}: {}", longUrl, finalShortUrl);
        return finalShortUrl;
    }

    private String shortUrlInDb(String longUrl) {
        String storedHash = urlRepository.returnHashForUrlIfExists(longUrl);
        if (storedHash != null) {
            return shortUrlTemplate + storedHash;
        } else {
            return null;
        }
    }

}
