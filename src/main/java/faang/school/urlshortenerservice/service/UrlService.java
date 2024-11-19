package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exeption.DataValidationException;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Value("${url.days-to-live}")
    private int daysToLive;

    @Transactional
    public String shortenUrl(String url) {
        Hash hash = hashCache.getHash();
        Url shortUrl = getShortUrl(url, hash);

        urlRepository.save(shortUrl);

        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{hash}")
                .buildAndExpand(hash)
                .toUriString();
    }

    @Cacheable(value = "urlCache")
    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        Url url = getUrlByHash(hash);
        return url.getUrl();
    }

    @Transactional(readOnly = true)
    public List<Url> findOldUrls() {
        return urlRepository.findOlderUrls(now().minusDays(daysToLive));
    }

    private Url getShortUrl(String url, Hash hash) {
        String hashStr = hash.getHash();
        return urlRepository.findByUrl(url).orElseGet(
                () -> Url.builder()
                        .url(url)
                        .hash(hashStr)
                        .build()
        );
    }

    private Url getUrlByHash(String hash) {
        return urlRepository.findUrlByHash(hash).orElseThrow(
                () -> new DataValidationException("Not found original url by passed short url.")
        );
    }
}
