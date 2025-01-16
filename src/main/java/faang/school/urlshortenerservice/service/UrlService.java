package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;

    @Transactional
    public UrlDto shortenUrl(UrlDto dto) {
        log.info("Try shortening url: {}", dto.getUrl());
        String hash = hashCache.getHash();
        Url urlHash = new Url(hash, dto.getUrl());
        log.info("Saving url to BD: {}", urlHash);
        urlRepository.save(urlHash);
        urlCacheRepository.saveToCache(urlHash);
        String urlShort = buildUri(hash);
        log.info("Got short url: {}", urlShort);
        return new UrlDto(urlShort);
    }

    public String getUrl(String hash) {
        log.info("Try getting url by hash: {}", hash);
        return urlCacheRepository.getUrlByHash(hash);
    }

    private String buildUri(String hash) {
        log.info("Building uri for hash: {}", hash);
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{hash}")
                .buildAndExpand(hash)
                .toUriString();
    }
}
