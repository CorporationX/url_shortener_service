package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
@Log4j2
public class UrlService {
    private final HashCache hashCache;

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String createShortUrl(@NotNull String url) {
        String hash = hashCache.getHash();
        if (hash == null) {
            log.error("Cannot get a hash for url: {}", url);
            throw new IllegalStateException("Failed to generate a hash for the URL.");
        }
        urlCacheRepository.saveUrl(hash, url);
        urlRepository.save(new Url(hash, url));
        return hash;
    }

    public String getUrlByHash(String hash) {
        String url = urlCacheRepository.findUrlByHash(hash);
        if (url != null) {
            return url;
        }
        return urlRepository.findByHash(hash)
                .map(entity -> {
                    urlCacheRepository.saveUrl(hash, entity.getUrl());
                    return entity.getUrl();
                })
                .orElseThrow(() -> new IllegalArgumentException("URL not found for hash: " + hash));
    }
}