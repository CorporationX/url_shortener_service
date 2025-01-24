package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.managers.HashCache;
import faang.school.urlshortenerservice.redis.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Data
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final String shortUrlTemplate = "http://example.url/api/v1/url/";

    @Transactional
    public String createShortUrl(String longUrl) {
        String shortUrl = shortUrlIfStored(longUrl);
        if (shortUrl != null) {
            return shortUrl;
        }

        String hash = hashCache.getHash();
        Url url = new Url(hash, longUrl);

        try {
            urlRepository.save(url);
        } catch (ConstraintViolationException e) {
            shortUrl = shortUrlIfStored(longUrl);
            if (shortUrl != null) {
                return shortUrl;
            }
        }
        urlCacheRepository.save(hash, longUrl);
        log.info("Short URL hash created: {}, longUrl: {}", hash, longUrl);
        return shortUrlTemplate + hash;
    }

    public String getOriginalUrl(String hash) {
        String originalUrl = urlCacheRepository.findUrlByHash(hash);
        if (originalUrl != null) {
            return originalUrl;
        }

        originalUrl = urlRepository.findUrlByHash(hash);
        if (originalUrl != null) {
            urlCacheRepository.save(hash, originalUrl);
            return originalUrl;
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found");
    }

    @Transactional
    public void cleanUpOldUrls() {
        List<String> oldHashes = urlRepository.deleteOldUrlsAndReturnHashes();
        hashRepository.saveBatch(oldHashes);
        urlCacheRepository.deleteBatch(oldHashes);
    }

    private String shortUrlIfStored(String longUrl) {
        String storedHash = urlRepository.returnHashForUrlIfExists(longUrl);
        if (storedHash != null) {
            return shortUrlTemplate + storedHash;
        } else {
            return null;
        }
    }
}

