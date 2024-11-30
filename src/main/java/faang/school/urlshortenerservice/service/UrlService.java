package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exeption.DataValidationException;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheService urlCacheService;
    private final HashService hashService;
    private final HashCache hashCache;

    @Transactional
    public String getShortUrl(String url) {
        Url shortUrl = urlRepository.findByUrl(url)
                .orElseGet(() -> buildUrl(url));

        urlRepository.save(shortUrl);
        urlCacheService.saveInCache(shortUrl);
        return buildUri(shortUrl);
    }

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        Optional<Object> urlOpt = urlCacheService.findByHash(hash);
        if (urlOpt.isPresent()) {
            return urlOpt.get().toString();
        }

        Url url = getUrlByHash(hash);
        if (url == null) {
            throw new DataValidationException("URL not found for hash: " + hash);
        }

        urlCacheService.saveInCache(url);
        return url.getUrl();
    }

    private Url buildUrl(String url) {
        Hash hash = hashService.ensureHashExists(hashCache.getHash());
        return Url.builder()
                .url(url)
                .hash(hash.getHash())
                .build();
    }

    private String buildUri(Url shortUrl) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{hash}")
                .buildAndExpand(shortUrl.getHash())
                .toUriString();
    }

    private Url getUrlByHash(String hash) {
        return urlRepository.findUrlByHash(hash).orElseThrow(
                () -> new DataValidationException("Not found original url by passed short url.")
        );
    }
}
