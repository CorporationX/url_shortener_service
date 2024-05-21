package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final LocalCache localCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    @Value("${url}")
    private String baseUrl;

    public String createShortenUrl(String longUrl) {
        String hash = urlCacheRepository.get(longUrl);
        if (hash != null) {
            return baseUrl + hash;
        }
        hash = localCache.getHash();
        Url url = Url.builder()
                .url(longUrl)
                .hash(hash)
                .build();
        urlRepository.save(url);
        urlCacheRepository.save(hash, longUrl);
        return baseUrl + hash;
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

        throw new EntityNotFoundException("URL not found for hash: " + hash);
    }
}
