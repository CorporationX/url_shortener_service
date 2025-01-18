package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    public String createShortUrl(String longUrl) {
        String hash = hashCache.getHash();

        Url url = new Url(hash, longUrl, LocalDateTime.now());
        urlRepository.save(url);

        urlCacheRepository.save(hash, longUrl);

        return "http://short.url/" + hash;
    }
}
