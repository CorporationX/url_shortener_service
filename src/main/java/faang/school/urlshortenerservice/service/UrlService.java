package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Cache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.localcache.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashGenerator hashGenerator;
    private final HashCache hash;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlValidator urlValidator;

    @CachePut(value = "cache", key = "#result.hash")
    public Url putUrl(String url) {
        urlValidator.validateUrlForPutUrl(url);
        String generatedHash = hash.getHash();
        Url entity = new Url(generatedHash, url, LocalDateTime.now());

        urlRepository.save(entity);

        Cache cacheEntity = new Cache(generatedHash, url, entity.getCreated_at());
        urlCacheRepository.save(cacheEntity);

        return entity;
    }

    public String getHash(String hash) {
        Cache cache = urlCacheRepository.findById(hash).orElse(null);
        String cacheUrl;
        if (cache != null) {
            cacheUrl = cache.getUrl();
        } else {
            cacheUrl = null;
        }

        if (cacheUrl == null) {
            cacheUrl = urlRepository.findUrlByHash(hash);
        }

        urlValidator.validateUrl(cacheUrl, hash);

        return cacheUrl;
    }


    public Cache getFromCache(String hash) {
        return urlCacheRepository.findById(hash).orElse(null);
    }
}
