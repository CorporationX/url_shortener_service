package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashEncoderProperties;
import faang.school.urlshortenerservice.hash.HashCacheImpl;
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
    public String findOriginalUrl(String hash) {
        validateHash(hash);
        return cacheRepository
            .get(hash)
            .orElseGet(() -> urlRepository
                .findByHash(hash)
                .map(longUrl -> {
                    cacheRepository.put(hash, longUrl);
                    return longUrl;
                })
                .orElseThrow(()-> new EntityNotFoundException("No url found for hash " + hash)));
    }

    private void validateHash(String hash) {
        if (hash.length() == hashEncoderProperties.getHashLength() &&
                hash.chars().allMatch(c -> hashEncoderProperties.getAlphabet().indexOf(c) >= 0)) {
            return;
        }
        String errorMessage = String.format(
            "Provided hash %s has wrong format, must be %d symbols long, contains digits, small, and BIG letters only",
            hash, hashEncoderProperties.getHashLength());
        throw new IllegalArgumentException(errorMessage);
    }
}
