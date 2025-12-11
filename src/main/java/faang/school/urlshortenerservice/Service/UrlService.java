package faang.school.urlshortenerservice.Service;

import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository cacheRepository;
    private final HashCache hashCache;

    public String generateShortUrl(String original) {
        String cachedHash = cacheRepository.getHashByOriginal(original);
        if (cachedHash != null) {
            return cachedHash;
        }

        String hash = hashCache.getHash();
        UrlEntity url = new UrlEntity(hash, original, LocalDateTime.now());
        urlRepository.save(url);
        cacheRepository.cache(url);

        return hash;
    }

    public String getOriginalUrl(String hash) {
        String original = cacheRepository.getOriginalByHash(hash);
        if (original != null) return original;

        return urlRepository.findByHash(hash)
                .map(entity -> {
                    cacheRepository.cache(entity);
                    return entity.getOriginalUrl();
                })
                .orElseThrow(() -> new DataAccessException("Hash does not exist") {
                });
    }
}