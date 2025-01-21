package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.HashResponseDto;
import faang.school.urlshortenerservice.dto.RequestDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${url.cleanup.interval}")
    private String cleanInterval;

    @Transactional
    public HashResponseDto getHash(RequestDto dto) {
        String existingHash = urlRepository.getByUrl(dto.getUrl());
        if (existingHash != null) {
            log.info("Hash for this link found in the database: {}", dto.getUrl());
            return new HashResponseDto(existingHash);
        }

        String hash = hashCache.getHash();
        Url url = new Url();
        url.setUrl(dto.getUrl());
        url.setHash(hash);
        urlRepository.save(url);

        log.info("Saved data to url table: {}", url);
        return new HashResponseDto(hash);
    }

    @Cacheable(value = "urls", key = "#hash", unless = "#result == null")
    @Transactional
    public String getUrl(String hash) {
        log.info("Fetching URL from database for hash: {}", hash);
        String resultFromDB = urlRepository.getByHash(hash);

        if (resultFromDB != null) {
            return resultFromDB;
        }

        log.warn("URL for this hash does not exist: {}", hash);
        throw new UrlNotFoundException("URL not found for hash: " + hash);
    }

    @Transactional
    public void cleanExpiredUrls() {
        List<String> freeHashes = urlRepository.getAndDeleteExpiredData(cleanInterval);
        List<Hash> hashes = freeHashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }

}
