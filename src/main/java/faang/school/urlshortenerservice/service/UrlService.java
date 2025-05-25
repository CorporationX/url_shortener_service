package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.RequestUlrDto;
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
    public ResponseUrlDto shorten(RequestUlrDto requestUlrDto) {
        String hash = hashCache.getHash();

        Url url = new Url();
        url.setUrl(requestUlrDto.getUrl());
        url.setHash(hash);
        urlRepository.save(url);

        urlCacheRepository.save(hash, requestUlrDto.getUrl(), ttlInSeconds);

        return getResponseUrlDto(hash);
    }

    @Transactional(readOnly = true)
    public ResponseUrlDto getOriginalUrl(String hash) {
        String cachedUrl = urlCacheRepository.get(hash);
        if (cachedUrl != null) {
            log.debug("Cache hit for hash: {}", hash);
            return getResponseUrlDto(cachedUrl);
        }

        String databaseUrl = urlRepository.findById(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash));

        return getResponseUrlDto(databaseUrl);
    }

    private ResponseUrlDto getResponseUrlDto(String hash) {
        return ResponseUrlDto.builder().shortUrl(baseUrlHttps + hash).build();
    }
}
