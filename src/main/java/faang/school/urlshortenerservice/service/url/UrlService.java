package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlEntity;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    
    public String getUrlByHash(String hash) {
        return urlCacheRepository.findUrlByHash(hash)
                .orElseGet(() -> {
                    log.debug("Cache miss for hash: {}", hash);
                    return urlRepository.findUrlByHash(hash)
                            .map(url -> {
                                log.debug("Found URL in database, caching for hash: {}", hash);
                                urlCacheRepository.saveUrl(hash, url);
                                return url;
                            })
                            .orElseThrow(() -> {
                                log.warn("URL not found for hash: {}", hash);
                                return new UrlNotFoundException(hash);
                            });
                });
    }

    public String createShortUrl(String originalUrl) {
        String hash = hashCache.getHash();
        urlRepository.save(new UrlEntity(hash, originalUrl));
        urlCacheRepository.saveUrl(hash, originalUrl);
        return "http://your-domain.com/" + hash;
    }
}
