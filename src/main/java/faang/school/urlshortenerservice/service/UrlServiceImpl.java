package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private String prefix;

    public UrlServiceImpl(UrlRepository urlRepository, UrlCacheRepository urlCacheRepository, HashCache hashCache,  @Value("${app.url-prefix}") String prefix) {
        this.urlRepository = urlRepository;
        this.urlCacheRepository = urlCacheRepository;
        this.hashCache = hashCache;
        this.prefix = prefix;
    }

    @Transactional
    @Override
    public String getHashFromUrl(UrlDto urlDto) {
        return prefix + findHashInDataBaseByBaseUrl(urlDto.getBaseUrl());
    }

    @Override
    public String getUrlFromHash(String hash) {
        return findUrlByHash(hash);
    }

    private String findHashInDataBaseByBaseUrl(String baseUrl) {
        return urlRepository.findByBaseUrl(baseUrl)
                .map(Url::getHash)
                .orElseGet(() -> generateNewHash(baseUrl));
    }

    private String generateNewHash(String baseUrl) {
        Hash hash = hashCache.getHash();
        urlRepository.save(new Url(hash.getHash(), baseUrl, LocalDateTime.now()));
        urlCacheRepository.save(hash.getHash(), baseUrl);
        return hash.getHash();
    }

    private String findUrlByHash(String hash) {
        return urlCacheRepository.getUrlByHash(hash)
                .orElseGet(() -> findUrlInDataBaseByHash(hash));
    }

    private String findUrlInDataBaseByHash(String hash) {
        return urlRepository.findByHash(hash)
                .map(Url::getBaseUrl)
                .orElseThrow(() -> new EntityNotFoundException("No url for hash: " + hash));
    }
}