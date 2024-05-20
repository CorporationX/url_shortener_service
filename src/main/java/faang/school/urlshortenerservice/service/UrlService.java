package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
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
}
