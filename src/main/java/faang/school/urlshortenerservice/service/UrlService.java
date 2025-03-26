package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final RedisHashPoolService hashPool;

    @Value("${ttl.hour.url}")
    private int ttlHours;

    @Transactional
    public String createShortUrl(String longUrl) {
        return urlRepository.findByUrl(longUrl)
                .map(Url::getHash)
                .orElseGet(() -> {
                    String hash = hashPool.acquire();
                    LocalDateTime expiresAt = LocalDateTime.now().plusHours(ttlHours);
                    urlRepository.save(new Url(hash, longUrl, expiresAt));
                    urlCacheRepository.save(hash, longUrl);
                    return hash;
                });
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        return urlCacheRepository.findByHash(hash)
                .or(() -> urlRepository.findByHash(hash)
                        .map(url -> {
                    urlCacheRepository.save(url.getHash(), url.getUrl());
                    return url.getUrl();
                }))
                .orElseThrow(() -> new ResourceNotFoundException("URL not found for hash: " + hash));
    }
}