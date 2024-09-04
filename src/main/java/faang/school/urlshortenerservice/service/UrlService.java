package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cashe.HashCache;
import faang.school.urlshortenerservice.exception.DuplicateUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String createShortUrl(String longUrl) {
        if (urlRepository.existsByUrl(longUrl)) {
            throw new DuplicateUrlException("This URL has already been shortened");
        }

        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(longUrl)
                .build();
        urlRepository.save(url);
        urlCacheRepository.save(url);
        return buildShortUrl(hash);
    }

    public String getLongUrl(String hash) {
        return urlCacheRepository.findLongUrlByHash(hash)
                .orElseGet(() -> {
                    Url url = urlRepository.findByHash(hash)
                            .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash));
                    urlCacheRepository.save(url);
                    return url.getUrl();
                });
    }

    private String buildShortUrl(String hash) {
        return "http://short.url/" + hash;
    }
}
