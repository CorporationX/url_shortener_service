package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;

    @Transactional
    public String createShortUrl(String longUrl, long userId) {
                    String hash = getAvailableHash();

                    Url url = new Url(hash, longUrl, userId);
                    urlRepository.save(url);
                    urlCacheRepository.save(hash, longUrl);

                    return hash;
    }

    @Transactional
    public String getOriginalUrl(String hash) {
        return urlCacheRepository.findByHash(hash)
                .or(() -> urlRepository.findByHash(hash).map(url -> {
                    urlCacheRepository.save(url.getHash(), url.getUrl());
                    return url.getUrl();
                }))
                .orElseThrow(() -> new ResourceNotFoundException("URL not found for hash: " + hash));
    }

    private String getAvailableHash() {
        return hashRepository.findAll()
                .stream()
                .findFirst()
                .map(hashObj -> {
                    hashRepository.delete(hashObj);
                    return hashObj.getHash();
                })
                .orElseThrow(() -> new RuntimeException("No available hashes"));
    }
}