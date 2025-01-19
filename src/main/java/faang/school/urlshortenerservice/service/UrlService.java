package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRedisCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashCacheService hashCacheService;
    private final UrlRepository urlRepository;
    private final UrlRedisCacheService urlRedisCacheService;
    private final UrlRedisCacheRepository urlRedisCacheRepository;

    @Value("${url-prefix}")
    private String urlPrefix;

    public ShortUrlDto createShortUrl(LongUrlDto longUrlDto) {
        String longUrl = longUrlDto.longUrl();
        String hash = hashCacheService.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(longUrl)
                .build();

        urlRepository.save(url);
        urlRedisCacheService.saveUrl(hash, longUrl);

        ShortUrlDto shortUrlDto = new ShortUrlDto(urlPrefix + hash);
        log.info("Created short url {} for long url {}.", shortUrlDto.shortUrl(), longUrlDto.longUrl());

        return shortUrlDto;
    }

    public String getLongUrl(String hash) {
        String longUrl;
        Optional<String> optionalUrl = urlRedisCacheRepository.getUrl(hash);

        if (optionalUrl.isPresent()) {
            longUrl = optionalUrl.get();
            log.info("Url {} for hash {} found in redis cache",longUrl, hash);
            return longUrl;
        } else {
            optionalUrl = urlRepository.findUrlByHash(hash);

            if (optionalUrl.isPresent()) {
                longUrl = optionalUrl.get();
                urlRedisCacheService.saveUrl(hash, longUrl);
                log.info("Url {} for hash {} found in database and saved in cache", longUrl, hash);
                return longUrl;
            }
        }

        throw new UrlNotFoundException(String.format("Url for hash %s was not found in cache and database", hash));
    }
}
