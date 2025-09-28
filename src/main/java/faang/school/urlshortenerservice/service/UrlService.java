package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlHashDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.utilities.HashCache;
import faang.school.urlshortenerservice.utilities.UrlRedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    @Value("${url.base-url}")
    private String baseUrl;

    private final HashCache hashCache;
    private final UrlRedisCache redisCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String create(UrlHashDto urlDto) {
        String url = urlDto.url();
        String hash = hashCache.getHash().getHash();
        Url newUrl = Url.builder().hash(hash).url(url).build();
        urlRepository.save(newUrl);
        redisCache.save(hash, url);
        return baseUrl + hash;
    }

    public String find(String hash) {
        return Optional.ofNullable(redisCache.get(hash))
                .orElseGet(() ->
                        urlRepository.findByHash(hash)
                                .orElseThrow(() -> new IllegalArgumentException("Hash is not exisits"))
                                .getUrl()
                );
    }

    public UrlResponseDto createShortUrl(String basiclUrl) {
        log.debug("Short URL was created for: {}", basiclUrl);

        Hash hash = hashCache.getHash();
        String stringFromHash = String.valueOf(hash);
        log.debug("Hash has been generated: {}", hash);

        Url urlEntity = new Url(stringFromHash, basiclUrl, LocalDateTime.now());

        urlRepository.save(urlEntity);
        log.debug("URL has been saved to database for hash: {}", hash);

        urlCacheRepository.saveUrl(stringFromHash, basiclUrl);
        log.debug("URL has been saved to Redis cache for hash: {}", hash);

        String shortUrl = baseUrl + hash;

        return new UrlResponseDto(shortUrl, basiclUrl, stringFromHash);
    }

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        return urlCacheRepository.findUrlByHash(hash)
                .orElseGet(() -> {
                    String url = urlCacheRepository.findUrlByHash(hash).orElseThrow(() -> {
                        String message = "URL not found for hash: %s".formatted(hash);
                        return new IllegalArgumentException(message);
                    });
                    urlCacheRepository.saveUrl(hash, url);
                    log.info("URL {} for hash {} was cached in Redis.", url, hash);
                    return url;
                });
    }

}
