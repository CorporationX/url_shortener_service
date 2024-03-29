package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String createShortUrl(String longUrl)  {
        String hash = hashCache.getHash();
        urlRepository.save(new Url(hash, longUrl, LocalDateTime.now()));
        urlCacheRepository.save(hash, longUrl);
        return hash;
    }

    public String getLongUrl(String hash) {
        String longUrl = urlCacheRepository.get(hash);
        if (longUrl != null) {
            return longUrl;
        }
        Url url = urlRepository.findByHash(hash);
        if (url != null) {
            return url.getUrl();
        }
        throw new EntityNotFoundException("URL " + hash + " not found");
    }
}