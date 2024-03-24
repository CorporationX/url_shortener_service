package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${url.short_url_prefix}")
    private String urlPrefix;

    public String getShortUrl(String url) {
        String hash = hashCache.getHash();
        urlRepository.save(new Url(hash, url, LocalDateTime.now()));
        urlCacheRepository.save(hash, url);
        return urlPrefix + hash;
    }

    public String getOriginalUrl(String hash) {
        String foundedUrl = urlCacheRepository.get(hash);
        if (foundedUrl == null) {
            Url url = urlRepository.findByHash(hash);
            if (url != null) {
                foundedUrl = url.getUrl();
            } else {
                throw new EntityNotFoundException("Url not found");
            }
        }
        return foundedUrl;
    }
}
