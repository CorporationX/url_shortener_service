package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.builder.UrlBuilder;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;
    private final UrlBuilder urlBuilder;

    @Value("${spring.data.redis.url_ttl}")
    private int urlTtlAtCache;

    @Value("${scheduler.urls_life_time_days}")
    private int urlTtlAtDB;

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        Url urlFromCache = urlCacheRepository.findByHash(hash);
        if (urlFromCache != null) {
            log.info("The URL was found at the cache {}", urlFromCache.getUrl());
            return urlFromCache.getUrl();
        }
        log.info("Url by hash {} not found at the cache", hash);
        Url urlFromDB = urlRepository.findById(hash)
                .orElseThrow(() -> new UrlNotfoundException("Url by hash: %s not found", hash));
        log.info("The URL was found at the DB {}", urlFromDB.getUrl());
        urlCacheRepository.saveUrl(urlFromDB, urlTtlAtCache);
        return urlFromDB.getUrl();
    }

    @Transactional
    public String createHash(String url) {
        String hash = hashCache.getHash();
        Url entity = new Url(hash, url, LocalDateTime.now().plusDays(urlTtlAtDB));
        urlRepository.save(entity);
        urlCacheRepository.saveUrl(entity, urlTtlAtCache);
        return urlBuilder.buildUrl(hash);
    }

    @Transactional
    public void cleanOldUrlsAndSavingFreedHashes() {
        List<Hash> oldUrls = urlRepository.deleteAndGetOldLines();
        log.info("{} old urls have been deleted", oldUrls.size());
        hashRepository.saveAll(oldUrls);
        log.info("{} released hashes are saved", oldUrls.size());
    }
}