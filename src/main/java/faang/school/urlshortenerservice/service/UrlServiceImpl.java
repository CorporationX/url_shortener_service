package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Value("${app.url-prefix}")
    private String prefix;

    @Transactional
    @Override
    public String getHashFromUrl(UrlDto urlDto) {
        return prefix + findHashByBaseUrl(urlDto.getBaseUrl());
    }

    private String findHashByBaseUrl(String baseUrl) {
        return urlCacheRepository.getHashByUrl(baseUrl)
                .orElseGet(() -> findHashInDataBaseByBaseUrl(baseUrl));
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
}