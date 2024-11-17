package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotfoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;

    @Value("${static_address}")
    private String staticAddress;

    @Value("${spring.data.redis.url_ttl}")
    private int urlTtlAtCache;

    @Value("${scheduler.urls_life_time_years}")
    private int urlTtlAtRepo;

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        Optional<Url> optionalUrl = Optional.ofNullable(urlCacheRepository.findByHash(hash));
        if (optionalUrl.isEmpty()) {
            log.info("Url by cache {} not found at cache", hash);
            optionalUrl = urlRepository.findById(hash);
        }
        Url url = optionalUrl.orElseThrow(() -> new UrlNotfoundException("Url by hash: %s not found", hash));
        log.info("Url founded {}", url.getUrl());
        urlCacheRepository.saveUrl(url, urlTtlAtCache);
        return url.getUrl();
    }

    @Transactional
    public String createHash(String url) {
        String hash = hashCache.getHash();
        Url entity = Url.builder()
                .url(url)
                .hash(hash)
                .build();
        urlRepository.save(entity);
        urlCacheRepository.saveUrl(entity, urlTtlAtCache);
        return staticAddress + hash;
    }

    @Transactional
    public void cleanOldUrlsAndSavingFreedHashes() {
        LocalDateTime minusYear = LocalDateTime.now().minusYears(urlTtlAtRepo);
        List<Hash> oldUrls = urlRepository.deleteOldUrlsAndGetFreedHashes(minusYear);
        log.info("{} old urls have been deleted", oldUrls.size());
        hashRepository.saveAll(oldUrls);
        log.info("{} released hashes are saved", oldUrls.size());
    }
}
