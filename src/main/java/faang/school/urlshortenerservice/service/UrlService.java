package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.hash.HashCacheFiller;
import faang.school.urlshortenerservice.model.HashEntity;
import faang.school.urlshortenerservice.model.UrlEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;
    private final HashCacheFiller hashCacheFiller;

    @Transactional
    public String shorten(String longUrl) {
        String hash = hashCache.getHash();
        hashCacheFiller.fillCacheIfNecessary();

        UrlEntity entity = new UrlEntity();
        entity.setUrlValue(longUrl);
        entity.setHashValue(hash);
        urlRepository.save(entity);

        urlCacheRepository.put(hash, longUrl);
        String shortUrl = createShortUrl(hash);

        log.info("Create shortened url {} <- {}", shortUrl, longUrl);
        return shortUrl;
    }

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
       String longUrl = urlCacheRepository.get(hash);
       if (Objects.nonNull(longUrl)) {
           log.info("Get original url {} <- {}", longUrl, createShortUrl(hash));
           return longUrl;
       }
       UrlEntity currentUrl = urlRepository.findByHashValue(hash).orElseThrow();
       String currentLongUrl = currentUrl.getUrlValue();
       urlCacheRepository.put(hash, currentLongUrl);

       log.info("Get original url {} <- {}", currentLongUrl, createShortUrl(hash));
       return currentLongUrl;
    }

    @Transactional
    public void cleanHashes() {
        LocalDateTime now = LocalDateTime.now();
        List<HashEntity> cleanedHash = urlRepository.deleteByValidatedAtBefore(now).stream()
                .map(UrlEntity::getHashValue)
                .map(HashEntity::new)
                .toList();

        log.info("Cleaned old hashes: {}", cleanedHash.size());
        hashRepository.saveAll(cleanedHash);
    }

    private String createShortUrl(String hash) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/")
                .path(hash)
                .toUriString();
    }
}
