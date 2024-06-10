package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    @Value("${url}")
    private String startUrl;

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Transactional
    public String createShortLink(UrlDto urlDto) {
        String hash = hashCache.getHash();
        Url newUrl = new Url();
        newUrl.setUrl(urlDto.getUrl());
        newUrl.setHash(hash);
        newUrl.setCreatedAt(Instant.now());
        urlRepository.save(newUrl);
        urlCacheRepository.save(hash, urlDto.getUrl());
        log.info("New url added: {}", newUrl);
        return startUrl + hash;
    }

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        String newHash = hash.replace(startUrl,"");
        String cacheUrl = urlCacheRepository.get(newHash);
        if (cacheUrl != null) {
            log.info("Cache url found: {}", cacheUrl);
            return cacheUrl;
        }
        Url url = urlRepository.findByHash(newHash);
        if (url != null) {
            log.info("Url found: {}", url);
            return url.getUrl();
        }
        throw new EntityNotFoundException("URL not found for hash: " + hash);
    }
}
