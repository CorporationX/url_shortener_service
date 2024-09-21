package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlDataValidationException;
import faang.school.urlshortenerservice.generate.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    public String getUrl(String hash) {
        UrlDto urlFromCache = urlCacheRepository.getUrlFromCache(hash);
        if (urlFromCache != null) {
            return urlFromCache.getUrl();
        }
        String urlByHash = urlRepository.getUrlByHash(hash);
        if (urlByHash != null) {
            return urlByHash;
        }
        throw new UrlDataValidationException("Url does not exist");

    }

    public String createUrl(UrlDto url) {
        String hash = hashCache.getHash();

        Url newUrl = Url.builder()
                .url(url.toString())
                .hash(hash)
                .createdAt(LocalDateTime.now())
                .build();
        urlRepository.createUrl(newUrl);
        urlCacheRepository.saveUrlInCache(hash, url);
        return hash;
    }
}
