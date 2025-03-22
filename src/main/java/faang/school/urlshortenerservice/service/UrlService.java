package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import faang.school.urlshortenerservice.utils.HashCache;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    @Value("${url.short-template}")
    private String shortUrlTemplate;

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Transactional
    public String createShortUrl(String longUrl) {
        String shortUrl = shortUrlIfStored(longUrl);
        if (shortUrl != null) {
            return shortUrl;
        }

        Hash hash = hashCache.getHash();
        Url url = new Url(hash.getHash(), longUrl);

        try {
            urlRepository.save(url);
        } catch (ConstraintViolationException e) {
            shortUrl = shortUrlIfStored(longUrl);
            if (shortUrl != null) {
                return shortUrl;
            }
        }
        urlCacheRepository.save(hash.getHash(), longUrl);
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
        List<Url> oldUrls = urlRepository.deleteOldUrlsAndReturnHashes();
        List<Hash> oldHashes = oldUrls.stream()
            .map(Url::getHash)
            .map(Hash::new)
            .toList();

        hashRepository.saveBatch(oldHashes);
        urlCacheRepository.deleteBatch(oldUrls.stream()
            .map(Url::getHash)
            .toList());
    }

    private String shortUrlIfStored(String longUrl) {
        String storedHash = urlRepository.returnHashForUrlIfExists(longUrl);
        return (storedHash != null) ? shortUrlTemplate + storedHash : null;
    }
}
