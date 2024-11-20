package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.RedisCache;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.scheduler.cleander.CleanerProperties;
import faang.school.urlshortenerservice.util.cache.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final CleanerProperties cleanerProperties;
    private final RedisCache redisCache;
    private final HashCache hashCache;

    @Override
    public String getLongUrlByHash(String hash) {
        return redisCache.getFromCache(hash)
                .or(() -> urlRepository.findUrlByHash(hash))
                .orElseThrow(() -> new UrlNotFoundException("URL not found by hash %s".formatted(hash)));
    }

    @Override
    @Transactional
    public String generateHashForUrl(String url) {
        String cachedHash = hashCache.getHash();

        redisCache.saveToCache(cachedHash, url);

        Url urlEntity = Url.builder()
                .hash(cachedHash)
                .url(url)
                .build();
        urlRepository.save(urlEntity);

        return url;
    }

    @Override
    @Transactional
    public void cleaningOldHashes() {
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(cleanerProperties.getLifeTimeDays());
        int batchSize = cleanerProperties.getBatchSize();

        List<String> expiredHashes;

        do {
            expiredHashes = urlRepository.deleteOutdatedUrls(expirationDate, batchSize);

            if (!expiredHashes.isEmpty()) {
                List<Hash> hashEntities = convertToHashEntities(expiredHashes);
                hashRepository.saveAll(hashEntities);
            }
        } while (!expiredHashes.isEmpty());
    }

    private List<Hash> convertToHashEntities(List<String> hashes) {
        return hashes.stream()
                .map(hash -> Hash.builder().hash(hash).build())
                .toList();
    }
}
