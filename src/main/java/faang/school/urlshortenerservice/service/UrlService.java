package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final int ttlInMinutes;

    public UrlService(UrlRepository urlRepository,
                      UrlCacheRepository urlCacheRepository,
                      HashCache hashCache,
                      @Value("${spring.data.redis.ttl.minutes}") int ttlInMinutes) {
        this.urlRepository = urlRepository;
        this.urlCacheRepository = urlCacheRepository;
        this.hashCache = hashCache;
        this.ttlInMinutes = ttlInMinutes;
    }

    public String createUrl(UrlDto urlDto) {
        log.info("Create url: {}", urlDto.getUrl());
        String hash = hashCache.getHash().getHash();
        Url url = new Url(hash, urlDto.getUrl());
        Url savedUrl = urlRepository.save(url);
        urlCacheRepository.save(savedUrl, ttlInMinutes);
        return hash;
    }

    public String getUrl(String hash) {
        log.info("Get url by hash: {}", hash);
        return urlCacheRepository.getByHash(hash)
                .orElseGet(() -> {
                    Url url = urlRepository.findById(hash)
                            .orElseThrow(() -> new EntityNotFoundException("Url by hash '" + hash + "' not found"));
                    urlCacheRepository.save(url, ttlInMinutes);
                    return url;
                })
                .getUrl();
    }
}
