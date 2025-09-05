package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.storage.HashMemoryCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashMemoryCache hashMemoryCache;

    @Cacheable(
            cacheManager = "redisCacheManager",
            cacheNames = "${app.cache.hash.prefix}",
            key = "#hash"
    )
    @Transactional(readOnly = true)
    public Url getUrlByHash(String hash) {
        return urlRepository.findById(hash)
                .orElseThrow(() -> {
                    String errorMessage = "Hash " + hash + " not found";
                    log.error(errorMessage);
                    return new HashNotFoundException(errorMessage);
                });
    }

    @CachePut(
            cacheManager = "redisCacheManager",
            cacheNames = "${app.cache.hash.prefix}",
            key = "#result.hash"
    )
    @Transactional
    public Url generateHash(String url) {
        String hash = hashMemoryCache.getHash();
        Url urlEntity = new Url();
        urlEntity.setHash(hash);
        urlEntity.setUrl(url);

        Url savedUrl = urlRepository.save(urlEntity);
        log.info("Url {} has been saved", savedUrl);

        return savedUrl;
    }
}
