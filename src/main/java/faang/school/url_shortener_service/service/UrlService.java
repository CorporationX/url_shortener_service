package faang.school.url_shortener_service.service;

import faang.school.url_shortener_service.cache.HashCache;
import faang.school.url_shortener_service.dto.UrlRequestDto;
import faang.school.url_shortener_service.dto.UrlResponseDto;
import faang.school.url_shortener_service.entity.Url;
import faang.school.url_shortener_service.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final RedisCacheManager cacheManager;

    @Value("${short.url.base}")
    private String baseUrl;
    @Value("${short.url.versionPath}")
    private String versionPath;


    @Transactional
    public UrlResponseDto createShortUrl(UrlRequestDto requestDto) {
        return urlRepository.findByUrl(requestDto.getOriginalUrl())
                .map(existingUrl -> {
                    cacheUrl(existingUrl.getHash(), existingUrl.getUrl());
                    return buildResponse(existingUrl.getHash());
                })
                .orElseGet(() -> {
                    String hash = hashCache.getHash();
                    Url url = new Url(hash, requestDto.getOriginalUrl(), OffsetDateTime.now());
                    urlRepository.save(url);
                    cacheUrl(hash, url.getUrl());
                    return buildResponse(hash);
                });
    }

    @Transactional(readOnly = true)
    public String getOriginalURL(String hash) {
        Cache cache = cacheManager.getCache("urls");
        String url = cache != null ? cache.get(hash, String.class) : null;
        if (url == null) {
            Url entity = urlRepository.findById(hash)
                    .orElseThrow(() -> new EntityNotFoundException("URL with hash %s not found".formatted(hash)));
            url = entity.getUrl();
        }

        cacheUrl(hash, url);
        return url;
    }

    private void cacheUrl(String hash, String url) {
        Cache cache = cacheManager.getCache("urls");
        if (cache != null) {
            cache.put(hash, url);
        }
    }

    private UrlResponseDto buildResponse(String hash) {
        return UrlResponseDto.builder()
                .shortUrl(buildShortUrl(hash))
                .build();

    }

    private String buildShortUrl(String hash) {
        return baseUrl + versionPath + hash;
    }
}