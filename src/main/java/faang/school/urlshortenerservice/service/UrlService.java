package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.url.HashNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.storage.HashMemoryCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashMemoryCache hashMemoryCache;
    private final HashService hashService;

    @Cacheable(cacheManager = "redisCacheManager",
            cacheNames = "${app.cache.hash.key-prefix}",
            key = "#hash")
    @Transactional(readOnly = true)
    public Url getUrlByHash(String hash) {
        return urlRepository.findById(hash)
                .orElseThrow(() -> {
                    String errorMsg = String.format("Hash %s not found", hash);
                    log.error(errorMsg);
                    return new HashNotFoundException(errorMsg);
                });
    }

    @CachePut(cacheManager = "redisCacheManager",
            cacheNames = "${app.cache.hash.key-prefix}",
            key = "#result.hash")
    @Transactional
    public Url generateHash(String url) {
        String hash = hashMemoryCache.getHash();
        Url urlEntity = new Url();
        urlEntity.setUrl(url);
        urlEntity.setHash(hash);

        Url savedUrl = urlRepository.save(urlEntity);
        log.info("Url {} has been saved", savedUrl);

        return savedUrl;
    }


    // TODO: нужно поправить
    @CacheEvict(cacheManager = "redisCacheManager",
            cacheNames = "${app.cache.hash.key-prefix}",
            key = "#result.hash")
    @Transactional
    public List<Hash> removeExpiredAndSaveHashes(LocalDateTime date) {
        List<Url> expiredUrls = urlRepository.deleteExpiredAndReturnHashes(date);

        List<Hash> hashes = expiredUrls.stream()
                .map(url -> new Hash(url.getHash()))
                .toList();
        return hashService.saveAll(hashes);
    }
}
