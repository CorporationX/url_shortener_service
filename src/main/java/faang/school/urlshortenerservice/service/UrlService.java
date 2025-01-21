package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashCacheService hashCacheService;
    private final UrlRepository urlRepository;
    private final UrlRedisCacheService urlRedisCacheService;

    @Value("${url-prefix}")
    private String urlPrefix;

    public String createShortUrl(LongUrlDto longUrlDto) {
        String longUrl = longUrlDto.longUrl();
        String hash = hashCacheService.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(longUrl)
                .build();

        urlRepository.save(url);
        urlRedisCacheService.saveUrl(hash, longUrl);

        String shortUrl = urlPrefix + hash;
        log.info("Created short url {} for long url {}.", shortUrl, longUrlDto.longUrl());

        return shortUrl;
    }

    public String getLongUrl(String hash) {
        return urlRedisCacheService.findByHash(hash)
                .map(longUrl -> {
                    log.debug("Url '{}' for hash '{}' found in redis cache", longUrl, hash);
                    return longUrl;
                }).orElseGet(() -> urlRepository.findByHash(hash)
                        .map(url -> {
                            String longUrl = url.getUrl();
                            urlRedisCacheService.saveUrl(hash, longUrl);
                            log.debug("Url '{}' for hash '{}' found in database and saved in cache", longUrl, hash);
                            return longUrl;
                        })
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Url for hash '%s' was not found.", hash))));
    }

    public List<Hash> deleteExpiredLinks(LocalDateTime expiredDate) {
        return urlRepository.deleteExpiredLinks(expiredDate).stream()
                .map(hash -> Hash.builder().hash(hash).build())
                .toList();
    }
}
