package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.Dto.UrlDto;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.DomainConfig;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final DomainConfig domainConfig;
    private final UrlCacheRepository urlCacheRepository;

    public String createShortLink(UrlDto urlDto) {
        String hash = hashCache.getHash();
        Url url = new Url(hash, urlDto.getUrl());
        urlRepository.save(url);
        urlCacheRepository.save(hash, urlDto.getUrl());
        return String.format("%s/%s", domainConfig.getBaseUrl(), hash);
    }

    @Cacheable(value = "urls", key = "#hash")
    public String getOriginalUrl(String hash) {
        return urlRepository.findById(hash).orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("URL с хэшом %s не существует", hash)))
                .getUrl();
    }

    public String getUrlByHash(String hash) {
        return null;
    }
}
