package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.OriginalUrlRequest;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Value("${server.host}")
    private String host;
    @Value("${server.port}")
    private String port;

    public String getUrlByHash(String hash) {
        log.info("Getting url by hash: {}", hash);
        Url url = urlCacheRepository.findByHash(hash);
        if (url != null) {
            return url.getUrl();
        }

        url = urlRepository.findById(hash).orElseThrow(
                () -> new EntityNotFoundException(String.format("Url by hash = %s doesn't exist.", hash)));
        log.info("Returning url = {} by hash = {}", url.getUrl(), hash);
        return url.getUrl();
    }

    @Transactional
    public String createShortUrl(OriginalUrlRequest request) {
        log.info("Starting to create a short url for {}", request.getUrl());
        String hash = hashCache.getHash();
        Url url = new Url(hash, request.getUrl());

        urlRepository.save(url);
        urlCacheRepository.save(url);
        log.info("Successfully creating a short url = {} for {}", hash, request.getUrl());
        return buildShortUrl(hash);
    }

    private String buildShortUrl(String hash) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(host)
                .port(port)
                .path("/{hash}")
                .buildAndExpand(hash)
                .toUriString();
    }
}
