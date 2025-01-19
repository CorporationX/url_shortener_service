package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.properties.UrlProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlProperties urlProperties;

    @Transactional
    public String generateShortUrl(UrlDto url) {
        return urlRepository.findHashByUrl(url.getUrl())
                .map(hash -> {
                    log.info("Hash found for URL {}: {}", url.getUrl(), hash);
                    urlCacheRepository.save(hash, url.getUrl());
                    log.info("Hash {} cached in Redis for URL {}", hash, url.getUrl());
                    return hash;
                })
                .orElseGet(() -> {
                    String hash = hashCache.getHash();
                    Url modelUrl = Url.builder()
                            .hash(hash)
                            .url(url.getUrl())
                            .createdAt(LocalDateTime.now())
                            .build();
                    urlRepository.save(modelUrl);
                    log.info("New URL saved in DB: {}", modelUrl);
                    urlCacheRepository.save(hash, url.getUrl());
                    log.info("New hash {} cached in Redis for URL {}", hash, url.getUrl());
                    hashCache.checkAndFillHashCache();
                    return hash;
                });
    }

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        return urlCacheRepository.findByHash(hash)
                .orElseGet(() -> {
                    String url = urlRepository.findUrlByHash(hash).orElseThrow(() -> {
                        String message = "URL not found for hash: %s".formatted(hash);
                        return new IllegalArgumentException(message);
                    });
                    urlCacheRepository.save(hash, url);
                    log.info("URL {} for hash {} was cached in Redis.", url, hash);
                    return url;
                });
    }

    @Transactional
    public void cleanOldUrls() {
        List<String> hashes = urlRepository
                .deleteAndGetOldUrls(LocalDate.now().minusYears(urlProperties.getYearToLife()));
        hashRepository.saveHashes(hashes.toArray(String[]::new));
        urlCacheRepository.removeHashes(hashes);
    }
}
