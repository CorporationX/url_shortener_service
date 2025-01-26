package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UniqueHashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final UniqueHashRepository hashRepository;
    private final UrlValidator validator;

    @Value("${url.original-path}")
    private String myUrl;

    @Transactional
    public String getUrlHash(UrlDto urlDto) {
        String longUrl = urlDto.getUrl();
        boolean checkIsValid = validator.isValidUrl(longUrl);
        if (!checkIsValid) {
            log.warn("Invalid URL {}", longUrl);
            return "Invalid URL " + urlDto.getUrl();
        }
        log.debug("LongUrl: {}", longUrl);
        StringBuilder builder = new StringBuilder();
        builder.append(myUrl);
        String hashFromBase = validator.urlExistsInBase(longUrl);
        if (hashFromBase != null) {
            log.info("Url {} already exists in base", longUrl);
            return builder.append(hashFromBase).toString();
        }
        String hash = hashCache.getHash();
        builder.append(hash);
        Url url = new Url();
        url.setHash(hash);
        url.setUrl(longUrl);
        urlRepository.save(url);
        log.info("LongUrl {} with hash {} was successful saved in base", longUrl, hash);
        urlCacheRepository.saveUrlInCache(hash, url);
        log.info("LongUrl {} with hash {} was successful saved in cash", longUrl, hash);
        return builder.toString();
    }

    @Transactional
    public String getOriginalUrl(String hash) {
        log.debug("hash: {}", hash);
        Url urlFromCache = urlCacheRepository.getUrlFromCache(hash);
        if (urlFromCache == null) {
            Url urlFromRepository = urlRepository.findByHash(hash)
                    .orElseThrow(() -> new DataValidationException("Cannot find longUrl from hash: " + hash));
            urlCacheRepository.saveUrlInCache(hash, urlFromRepository);
            log.info("Url {} was become from base", urlFromRepository.getUrl());
            return urlFromRepository.getUrl();
        }
        log.info("Url {} was become from cache", urlFromCache.getUrl());
        return urlFromCache.getUrl();
    }

    @Transactional
    public void cleaner() {
        List<Hash> hashes = urlRepository.getAndDeleteAfterOneYear();
        hashRepository.saveAll(hashes);
    }

}
