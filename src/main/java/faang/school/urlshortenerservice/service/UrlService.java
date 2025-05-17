package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.handler.UrlNotFoundException;
import faang.school.urlshortenerservice.properties.ShortenerProperties;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final ShortenerProperties properties;

    public String createShortUrl(String originalUrl) {
        return urlRepository.findByUrl(originalUrl)
                .map(u -> properties.getBaseUrl() + "/" + u.getHash())
                .orElseGet(() -> {
                    String hash = hashCache.getHash();
                    Url url = new Url();
                    url.setUrl(originalUrl);
                    url.setHash(hash);
                    url.setCreatedAt(Timestamp.from(Instant.now()));

                    urlRepository.save(url);
                    urlCacheRepository.save(hash, originalUrl);

                    return properties.getBaseUrl() + "/" + hash;
                });
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

    public String createOrResolveUrl(String url) {
        String baseUrl = properties.getBaseUrl().toString();

        if (url.startsWith(baseUrl)) {
            String hash = url.substring(url.lastIndexOf("/") + 1);
            return resolveUrl(hash);
        }

        return createShortUrl(url);
    }
}
