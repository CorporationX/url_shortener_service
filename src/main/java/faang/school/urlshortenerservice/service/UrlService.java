package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlShortenerRequest;
import faang.school.urlshortenerservice.dto.UrlShortenerResponse;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.utils.HashCache;
import faang.school.urlshortenerservice.utils.UrlValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@Slf4j
@CacheConfig(cacheNames = "urls")
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlValidator validator;
    private final String hostUrl;

    @Value("${spring.shortlink-interlayer}")
    private final String interlayer;

    @Transactional
    public UrlShortenerResponse create(UrlShortenerRequest request) {
        log.info("creating a short link");
        validator.isUrl(request.endPoint());

        log.info("getting a hash");
        Hash hash = hashCache.getHash();
        Url url = Url.builder()
                .url(request.endPoint())
                .hash(hash.getHash())
                .createdAt(OffsetDateTime.now())
                .build();

        log.info("saving shortlink in DB");
        urlRepository.save(url);
        log.info("caching shortlink");
        urlCacheRepository.cacheCouple(hash.getHash(), url.getUrl());

        String shortLink = hostUrl + interlayer + "/" + hash.getHash();

        return new UrlShortenerResponse(shortLink, request.endPoint());
    }

    @Transactional
    public UrlShortenerResponse getEndPoint(String hash) {
        log.info("getting endPoint");
        String shortLink = hostUrl + hash;
        String cachedEndPoint = urlCacheRepository.getEndPointByHash(hash);
        if (cachedEndPoint == null) {
            Url url = urlRepository.getUrlByHash(hash).orElseThrow(() -> new EntityNotFoundException("url not found"));
            urlCacheRepository.cacheCouple(hash, url.getUrl());
            return new UrlShortenerResponse(shortLink, url.getUrl());
        }
        return new UrlShortenerResponse(shortLink, cachedEndPoint);
    }
}