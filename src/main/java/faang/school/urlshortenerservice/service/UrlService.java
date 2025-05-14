package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.handler.UrlNotFoundException;
import faang.school.urlshortenerservice.properties.ShortenerProperties;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final ShortenerProperties properties;

    public String createShortUrl(String originalUrl) {
        Optional<UrlEntity> existing = urlRepository.findByUrl(originalUrl);
        if (existing.isPresent()) {
            return properties.getBaseUrl() + "/" + existing.get().getHash();
        }


        String hash = hashCache.getHash();

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setUrl(originalUrl);
        urlEntity.setHash(hash);
        urlEntity.setCreatedAt(Timestamp.from(Instant.now()));

        urlRepository.save(urlEntity);
        urlCacheRepository.save(hash, originalUrl);

        return properties.getBaseUrl() + "/" + hash;
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
