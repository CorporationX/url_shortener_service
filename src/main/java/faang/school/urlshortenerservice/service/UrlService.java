package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exceptions.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.utils.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;

    @Value("${spring.datasource.url}")
    private String baseUrl;

    public String getShortUrl(UrlDto urlDto) {
        String url = urlDto.getUrl();
        String hash = hashCache.getHashFromCache();

        UrlDto newShortUrl = new UrlDto(url, hash, LocalDateTime.now());

        urlCacheRepository.save(hash, url);
        urlRepository.save(urlMapper.toEntity(newShortUrl));

        String shortUrl = baseUrl + "/url/" + hash;
        log.info("Generated short URL: {}", shortUrl);
        return shortUrl;
    }

    public String redirectToRealUrl(String hash) {
        String url = getUrlFromCacheOrDb(hash);
        log.info("Redirecting to: {}", url);
        return url;
    }

    private String getUrlFromCacheOrDb(String hash) {
        String url = urlCacheRepository.findByHashInRedis(hash);
        if (url != null) {
            log.info("Found in cache: {}", url);
            return url;
        }

        log.info("Not found in cache, fetching from DB: {}", hash);
        return urlRepository.findByHash(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotFoundException("Url not found for hash " + hash));
    }
}
