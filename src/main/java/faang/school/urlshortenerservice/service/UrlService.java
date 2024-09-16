package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.URLDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.URL;
import faang.school.urlshortenerservice.exception.ExceptionMessage;
import faang.school.urlshortenerservice.exception.handler.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.URLCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final URLCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;

    @Value("${url.host}")
    private String host;

    @Transactional
    public String createShortLink(URLDto urlDto) {
        String hash = getHashIfExistsInDBOrHash(urlDto.getUrl());

        if (hash == null) {
            hash = generateAndSaveNewUrl(urlDto);
        }

        String resultHash = host + hash;
        log.info("Hash is ready - {} sent to client", resultHash);

        return resultHash;
    }

    public String getUrlByHash(String hash) {
        int lastIndex = hash.lastIndexOf('/');
        String actualHash = hash.substring(lastIndex + 1);
        log.info("Split the hash obtained - {} and received - {}", hash, actualHash);

        return urlCacheRepository.findUrlByHash(actualHash)
                .map(cachedUrl -> {
                    log.info("URL - {} found in cache", cachedUrl);
                    return cachedUrl;
                })
                .or(() -> urlRepository.findUrlByHash(actualHash)
                        .map(urlInBD -> {
                            log.info("URL - {} not cached", urlInBD);
                            log.info("URL - {} obtained from the BD", urlInBD);
                            urlCacheRepository.save(urlInBD, actualHash);
                            return urlInBD;
                        }))
                .orElseThrow(() -> new UrlNotFoundException(ExceptionMessage.URL_NOT_FOUND + actualHash));
    }

    @Transactional
    public void deleteOldURL(String removedPeriod) {
        urlRepository.getHashAndDeleteURL(removedPeriod).ifPresent(hashes -> {
            if (hashes.isEmpty()) {
                log.info("No old URL in database.");
            } else {
                hashRepository.saveAll(hashes.stream()
                        .map(Hash::new)
                        .toList());
                log.info("Deleted old URLs and saved {} hashes.", hashes.size());
            }
        });
    }

    private String getHashIfExistsInDBOrHash(String url) {
        return urlCacheRepository.findHashByUrl(url)
                .or(() -> urlRepository.findHashByUrl(url)
                        .map(hashInBD -> {
                            urlCacheRepository.save(url, hashInBD);
                            log.info("Hash saved again in Cash.");
                            return hashInBD;
                        }))
                .orElse(null);
    }

    private String generateAndSaveNewUrl(URLDto urlDto) {
        String newHash = hashCache.getHash();
        log.info("Get generated hash {}", newHash);

        URL newUrl = URL.builder()
                .url(urlDto.getUrl())
                .hash(newHash)
                .build();
        log.info("New URL {} created.", newUrl);

        try {
            urlRepository.save(newUrl);
            log.info("New URL {} save in DB", newUrl);
            urlCacheRepository.save(urlDto.getUrl(), newHash);
            log.info("New URL {} and hash {} save in Cash", newUrl.getUrl(), newUrl.getHash());
        } catch (DataIntegrityViolationException e) {
            log.error(ExceptionMessage.EXCEPTION_IN_SAVE + e.getMessage());
        }
        return newHash;
    }
}