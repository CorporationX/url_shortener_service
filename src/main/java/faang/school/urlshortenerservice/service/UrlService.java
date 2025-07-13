package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.managers.HashCache;
import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashJdbcRepository hashJdbcRepository;
    private final String shortUrlTemplate = "http://faang.url/api/v1/url/";

    @Transactional
    public String shortUrl(String longUrl) {

        String shortUrl = shortUrlInDb(longUrl);
        if (shortUrl != null) {
            return shortUrl;
        }

        String hash = hashCache.getHash();
        Url url = new Url(hash, longUrl);

        try {
            urlRepository.save(url);
        } catch (ConstraintViolationException e) {
            return shortUrlInDb(longUrl);
        }

        urlCacheRepository.save(hash, longUrl);

        return shortUrlTemplate + hash;
    }


    private String shortUrlInDb(String longUrl) {
        String storedHash = urlRepository.returnHashByUrlIfExists(longUrl);
        if (storedHash != null) {
            return shortUrlTemplate + storedHash;
        } else {
            return null;
        }
    }

    public String getLongUrl(String hash) {
        String longUrl = urlCacheRepository.findUrlByHash(hash);
        if (longUrl != null) {
            return longUrl;
        }
        longUrl = urlRepository.findUrlByHash(hash);

        if (longUrl != null){
            urlCacheRepository.save(hash, longUrl);
            return longUrl;
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found");
    }

    @Transactional
    public void cleanOldUrls() {
        List<String> oldHashes = urlRepository.deleteOldUrlsAndReturnHashes();
        hashJdbcRepository.saveBatch(oldHashes);
        urlCacheRepository.deleteOldHashes(oldHashes);
    }
}
