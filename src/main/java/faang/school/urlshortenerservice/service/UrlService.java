package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exeption.DataValidationException;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashService hashService;
    private final HashCache hashCache;

    @Transactional
    public String getShortUrl(String url) {
        Url shortUrl = urlRepository.findByUrl(url)
                .orElseGet(() -> buildUrl(url));

        urlRepository.save(shortUrl);
        return buildUri(shortUrl);
    }

    @Cacheable(value = "urlCache")
    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        Url url = getUrlByHash(hash);
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
