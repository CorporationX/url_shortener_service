package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.property.LifecycleProperty;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.CacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final CacheRepository cacheRepository;
    private final LifecycleProperty lifecycleProperties;

    @Transactional
    public String generateShortUrl(String url) {
        return urlRepository.findHashByUrl(url)
            .map(hash -> {
                log.info("Hash founded. Hash - {}. url - {}", hash, url);
                return hash;
            })
            .orElseGet(() -> createdNewHash(url));
    }

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        return cacheRepository.findByHash(hash)
            .filter(url -> !url.isEmpty())
            .orElseThrow(() -> new IllegalArgumentException(String.format("URL not found for hash: %s", hash)));
    }

    @Transactional
    public void cleaningExpiredUrls() {
        List<Hash> deletedUrls = urlRepository.cleaningExpiredUrls(lifecycleProperties.getDeadLine());

        if(!deletedUrls.isEmpty()) {
            cacheRepository.removeHashes(deletedUrls.stream()
                .map(Hash::getHash)
                .toList()
            );
        }
    }

    private String createdNewHash(String url) {
        String hash = hashCache.getHash();

        urlRepository.save(createUrl(hash, url));
        cacheRepository.save(hash, url);
        log.info("Add cached. Hash - {}. Url - {}", hash, url);
        hashCache.ensureCacheIsFilled();

        return hash;
    }

    private Url createUrl(String hash, String url) {
        return new Url(hash, url, LocalDateTime.now());
    }
}
