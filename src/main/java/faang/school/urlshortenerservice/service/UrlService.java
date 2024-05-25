package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class UrlService {

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    @Value("${app.url-prefix}")
    private String urlPrefix;

    public String redirectOriginalUrl(String hash) {
        return urlCacheRepository.getUrlByHash(hash).orElseGet(
                () -> urlRepository.getUrlByHash(hash).map(Url::getBaseUrl).orElseThrow(
                        () -> new EntityNotFoundException("URL not found for hash: " + hash)));
    }

    @Transactional
    public String convertToShortUrl(UrlDto urlDto) {
        String urlPostfix = findHashByUrl(urlDto.getBaseUrl());
        return urlPrefix + urlPostfix;
    }

    private String findHashByUrl(String baseUrl) {
        return urlCacheRepository.existsUrl(baseUrl)
                .orElseGet(() -> findHashInDatabase(baseUrl));
    }

    private String findHashInDatabase(String baseUrl) {
        log.info("URL not found in cache");
        return urlRepository.findByUrl(baseUrl)
                .map(Url::getHash)
                .orElseGet(() -> generateNewHashAndSave(baseUrl));
    }

    private String generateNewHashAndSave(String baseUrl) {
        log.info("Url not found in data base");
        Hash hash = hashCache.getHash();
        urlRepository.save(new Url(hash.getUniqueHash(), baseUrl, LocalDateTime.now()));
        urlCacheRepository.save(hash.getUniqueHash(), baseUrl);
        log.info("Url added in data base and cache");
        return hash.getUniqueHash();
    }

}