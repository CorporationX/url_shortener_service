package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Value("${server.url}")
    private String domain;

    public String createShortUrl(String longUrl) {
        String hash = hashCache.getHash();

        UrlEntity urlEntity = new UrlEntity(hash, longUrl, LocalDateTime.now());
        urlRepository.save(urlEntity);

        urlCacheRepository.saveUrl(hash, longUrl);

        return domain + "/" + hash;
    }

    public String getOriginalUrl(String hash) {
        String cachedUrl = urlCacheRepository.getUrl(hash);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        return urlRepository.findById(hash)
                .map(UrlEntity::getUrl)
                .orElseThrow(() -> new UrlNotFoundException(hash));
    }
}


