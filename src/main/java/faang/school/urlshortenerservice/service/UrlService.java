package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository cacheRepository;
    private final HashCache hashCache;

    @Transactional
    public String generateShortUrl(String original) {
        log.info("Checking whether short URL exists in cache");
        String cachedHash = cacheRepository.getHashByOriginal(original);
        if (cachedHash != null) {
            return cachedHash;
        }
        log.info("Short URL does not exist, creating");
        String hash = hashCache.getHash();
        UrlEntity url = new UrlEntity(hash, original, LocalDateTime.now());
        urlRepository.save(url);
        cacheRepository.cache(url);

        return hash;
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        log.info("Checking whether original URL exists in cache");
        String original = cacheRepository.getOriginalByHash(hash);
        if (original != null) return original;
        log.info("Checking whether original URL exists in DB");
        return urlRepository.findByHash(hash)
                .map(entity -> {
                    cacheRepository.cache(entity);
                    return entity.getOriginalUrl();
                })
                .orElseThrow(() -> new DataAccessException("Specified short URL does not exist") {
                });
    }
}