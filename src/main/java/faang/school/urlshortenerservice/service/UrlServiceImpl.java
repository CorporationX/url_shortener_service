package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.hash.HashCacheImpl;
import faang.school.urlshortenerservice.config.HashEncoderProperties;
import faang.school.urlshortenerservice.repository.CacheRepositoryRedisImpl;
import faang.school.urlshortenerservice.repository.UrlRepositoryJdbcImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlRepositoryJdbcImpl urlRepository;
    private final CacheRepositoryRedisImpl cacheRepository;
    private final HashCacheImpl hashCache;
    private final HashEncoderProperties hashEncoderProperties;

    @Override
    @Transactional
    public String shortAndReturn(String longUrl) {
        String hash = hashCache.getHash();
        urlRepository.save(hash, longUrl);
        cacheRepository.put(hash, longUrl);
        return hash;
    }

    @Override
    public String findOriginal(String hash) {
        validateHash(hash);
        return cacheRepository
                .get(hash)
                .orElseGet(() -> urlRepository
                        .findByHash(hash)
                        .map(longUrl -> {
                            cacheRepository.put(hash, longUrl);
                            return longUrl;
                        })
                        .orElseThrow(EntityNotFoundException::new));
    }

    private void validateHash(String hash) {
        if (hash.length() == hashEncoderProperties.getHashLength() &&
                hash.chars().allMatch(c -> hashEncoderProperties.getAlphabet().indexOf(c) >= 0)) {
            return;
        }
        throw new IllegalArgumentException("Provided hash wrong format");
    }
}
