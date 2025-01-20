package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    public String getOriginalUrl(String hash) {
        // Redis
        String url = urlCacheRepository.findByHash(hash);
        if (url != null) {
            return url;
        }

        // DB
        return urlRepository.findByHash(hash)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash));
    }

    public String createShortUrl(String originalUrl) {
        String hash = hashCache.getHash();

        UrlEntity urlEntity = new UrlEntity(hash, originalUrl);
        urlRepository.save(urlEntity);
        urlCacheRepository.save(hash, originalUrl);

        return generateShortUrl(hash);
    }

    private String generateShortUrl(String hash) {
        return "http://short.url/" + hash;
    }
}