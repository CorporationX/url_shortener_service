package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.URLDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.URL;
import faang.school.urlshortenerservice.exception.ExceptionMessage;
import faang.school.urlshortenerservice.exception.url.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.URLCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final URLCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;
    private final HashMapper hashMapper;

    @Value("${url.host}")
    private String host;

    @Transactional
    public HashDto createShortLink(URLDto urlDto) {
        String hash = getHashIfExistsInDBOrHash(urlDto.getUrl());

        if (hash == null) {
            hash = generateAndSaveNewUrl(urlDto);
        }
        return hashMapper.toDto(new Hash(host + hash));
    }

    public String getUrlByHash(String hash) {
        int lastIndex = hash.lastIndexOf('/');
        String actualHash = hash.substring(lastIndex + 1);

        return urlCacheRepository.findUrlByHash(actualHash)
                .or(() -> urlRepository.findUrlByHash(actualHash)
                        .map(urlInBD -> {
                            urlCacheRepository.save(urlInBD, actualHash);
                            return urlInBD;
                        }))
                .orElseThrow(() -> new UrlNotFoundException(ExceptionMessage.URL_NOT_FOUND + actualHash));
    }

    @Transactional
    public void deleteOldURL() {
        List<String> hashes = urlRepository.getHashAndDeleteURL();
        if (hashes.isEmpty()) {
            log.info("No old URL in database");
            return;
        }
        hashRepository.saveAll(hashes.stream()
                .map(Hash::new)
                .toList());
        log.info("Deleted old URLs and saved {} hashes.", hashes.size());
    }

    private String getHashIfExistsInDBOrHash(String url) {
        return urlCacheRepository.findHashUrlByUrl(url)
                .or(() -> urlRepository.findHashByUrl(url))
                .orElse(null);
    }

    private String generateAndSaveNewUrl(URLDto urlDto) {
        String newHash = hashCache.getHash();

        URL newUrl = URL.builder()
                .url(urlDto.getUrl())
                .hash(newHash)
                .build();
        try {
            urlRepository.save(newUrl);
            urlCacheRepository.save(urlDto.getUrl(), newHash);
        } catch (DataIntegrityViolationException e) {
            log.error(ExceptionMessage.EXCEPTION_IN_SAVE + e.getMessage());
        }
        return newHash;
    }
}