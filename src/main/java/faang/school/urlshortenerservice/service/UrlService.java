package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String createShortUrl(String url) {
        String hash = hashCache.getHash();
        urlRepository.save(hash, url);
        urlCacheRepository.putToCache(hash, url);
        return hash;
    }


    public String getOriginalUrl(String hash) {
        String originalUrl;

        if ((originalUrl = getFromCache(hash)) != null) {
            return originalUrl;

        } else if ((originalUrl = getFromDataBase(hash)) != null) {
            urlCacheRepository.putToCache(hash, originalUrl);
            return originalUrl;
        }

        throw new EntityNotFoundException("Url with hash " + hash + " not found");
    }

    public String getFromDataBase(String hash) {
        return urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("Url with hash " + hash + " not found"));
    }

    private String getFromCache(String hash) {
        return urlCacheRepository.getFromCache(hash);
    }
}
