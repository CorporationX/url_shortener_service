package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.HashResponseDto;
import faang.school.urlshortenerservice.dto.RequestDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.impl.UrlCacheRepositoryImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlCacheRepositoryImpl urlCacheRepositoryImpl;

    @Transactional
    public HashResponseDto getHash(RequestDto dto) {
        if (getExistingHashByUrl(dto.getUrl()) != null) {
            return new HashResponseDto(getExistingHashByUrl(dto.getUrl()));
        }
        String hash = hashCache.getHash();
        Url url = new Url();
        url.setUrl(dto.getUrl());
        url.setHash(hash);
        urlRepository.save(url);
        log.info("saved data to url table : {}", url);
        urlCacheRepositoryImpl.add(dto.getUrl(), hash);
        return new HashResponseDto(hash);
    }

    @Transactional
    public String getUrl(String hash) {
        String resultFromCache = urlCacheRepositoryImpl.getUrl(hash);
        if (resultFromCache != null) {
            log.info("Result found from Redis cache: {}", hash);
            return resultFromCache;
        }

        String resultFromDB = urlRepository.getByHash(hash);
        if (resultFromDB != null) {
            log.info("Url found in the database: {}", hash);
            return resultFromDB;
        }
        log.warn("Url for this hash does not exist: {}", hash);
        throw new UrlNotFoundException("URL not found for hash: " + hash);
    }

    @Transactional
    public void clean() {
        List<String> freeHashes = urlRepository.getAndDeleteExpiredData();
        List<Hash> hashes = freeHashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }

    private String getExistingHashByUrl(String url) {
        String resultFromCache = urlCacheRepositoryImpl.getHash(url);
        if (resultFromCache != null) {
            log.info("Hash for this link already exists in Redis: {}", url);
            return resultFromCache;
        }

        String resultFromDB = urlRepository.getByUrl(url);
        if (resultFromDB != null) {
            log.info("Hash for this link found in the database: {}", url);
            return resultFromDB;
        }
        log.warn("Hash for this link does not exist: {}", url);
        return null;
    }

}
