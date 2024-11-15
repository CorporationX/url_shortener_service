package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.model.dto.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.cache.HashCache;
import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${short_url.domain}")
    private String shortUrlDomain;

    @Override
    @Transactional
    public String createShortUrl(UrlDto urlDto) {
        Url url = Url.builder()
                .url(urlDto.getOriginalUrl())
                .hash(hashCache.getHash()).build();
        urlCacheRepository.save(url.getHash(), urlDto.getOriginalUrl());
        urlRepository.save(url);
        return shortUrlDomain + url.getHash();
    }

    @Override
    public String getOriginalUrl(String hash) {
        String originalUrl = (String) urlCacheRepository.get(hash);
        if (originalUrl != null) {
            return originalUrl;
        }
        Url url = urlRepository.findUrlByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("Can't find url with hash: " + hash));
        return url.getUrl();
    }
}
