package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.Dto.UrlRequestDto;
import faang.school.urlshortenerservice.Dto.UrlResponseDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Transactional
    public UrlResponseDto createShortUrl(UrlRequestDto urlRequestDto) {
        String originalUrl = urlRequestDto.getUrl();

        Url existingUrl = urlRepository.findByUrl(originalUrl)
                .orElse(null);

        if (existingUrl != null) {
            log.info("URL already exists with hash: {}", existingUrl.getHash());
            return createResponseDto(existingUrl);
        }

        String hash = hashCache.getHash();

        Url url = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .build();
        Url savedUrl = urlRepository.save(url);
        log.info("URL saved to database with hash: {}", savedUrl.getHash());

        urlCacheRepository.save(hash, originalUrl);
        log.info("URL saved to cache with hash: {}", savedUrl.getHash());

        return createResponseDto(savedUrl);
    }

    @Transactional
    public String getOriginalUrl(String hash) {
        String cachedUrl = urlCacheRepository.get(hash);
        if (cachedUrl != null) {
            return cachedUrl;
        }
        Url url = urlRepository.findByHashOrThrow(hash);
        urlCacheRepository.save(hash, url.getUrl());

        return url.getUrl();
    }

    private UrlResponseDto createResponseDto(Url url) {
        return new UrlResponseDto(
                url.getHash(),
                url.getUrl(),
                url.getHash());
    }
}
