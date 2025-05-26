package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${hash.cache.ttl-in-seconds:172800}") //172800 два дня в секундах
    private long ttlInSeconds;

    @Value("${app.base-url}")
    private String baseUrlHttps;

    @Transactional
    public ResponseUrlDto shorten(RequestUrlDto requestUrlDto) {
        String hash = hashCache.getHash();

        Url url = new Url();
        url.setUrl(requestUrlDto.getUrl());
        url.setHash(hash);
        urlRepository.save(url);

        urlCacheRepository.save(hash, requestUrlDto.getUrl(), ttlInSeconds);

        return ResponseUrlDto.builder()
                .originalUrl(requestUrlDto.getUrl())
                .shortUrl(baseUrlHttps + hash)
                .build();
    }

    @Transactional(readOnly = true)
    public ResponseUrlDto getOriginalUrl(String hash) {
        String url = getUrlFromCacheOrDatabase(hash);
        return ResponseUrlDto.builder()
                .originalUrl(url)
                .build();
    }

    @Transactional(readOnly = true)
    public ResponseUrlDto getUrlInfo(String hash) {
        String url = getUrlFromCacheOrDatabase(hash);
        return ResponseUrlDto.builder()
                .originalUrl(url)
                .shortUrl(baseUrlHttps + hash)
                .build();
    }

    private String getUrlFromCacheOrDatabase(String hash) {
        String cachedUrl = urlCacheRepository.get(hash);
        if (cachedUrl != null) {
            log.debug("Cache hit for hash: {}", hash);
            return cachedUrl;
        }

        String databaseUrl = urlRepository.findById(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash));

        urlCacheRepository.save(hash, databaseUrl, ttlInSeconds);
        return databaseUrl;
    }
}