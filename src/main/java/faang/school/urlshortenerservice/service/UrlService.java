package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.handler.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${shortener.base-url}")
    private String baseUrl;

    public String createShortUrl(String originalUrl) {
        String hash = hashCache.getHash();
        Url url = new Url();
        url.setUrl(originalUrl);
        url.setHash(hash);
        url.setCreatedAt(Timestamp.from(Instant.now()));
        urlRepository.save(url);

        urlCacheRepository.save(hash, originalUrl);
        return null;
    }

    public String resolveUrl(String hash) {
        return urlCacheRepository.find(hash)
                .orElseGet(() ->
                        urlRepository.findById(hash)
                                .map(found -> {
                                    urlCacheRepository.save(hash, found.getUrl());
                                    return found.getUrl();
                                })
                                .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash))
                );
    }
}
