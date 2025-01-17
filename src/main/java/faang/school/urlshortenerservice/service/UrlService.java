package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.RequestDto;
import faang.school.urlshortenerservice.dto.HashResponseDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.impl.UrlCacheRepositoryImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
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
        urlCacheRepositoryImpl.add(dto.getUrl(), hash);
        return new HashResponseDto(hash);
    }

    @Transactional
    public UrlResponseDto getUrl(String hash) {
        String resultFromCache = urlCacheRepositoryImpl.getUrl(hash);
        if (resultFromCache != null) {
            log.info("Result found from Redis cache: {}", hash);
            return new UrlResponseDto(resultFromCache);
        }

        String resultFromDB = urlRepository.getByHash(hash);
        if (resultFromDB != null) {
            log.info("Url found in the database: {}", hash);
            return new UrlResponseDto(resultFromDB);
        }
        log.warn("Url for this hash does not exist: {}", hash);
        throw new UrlNotFoundException("URL not found for hash: " + hash);
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
