package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashService hashService;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String shorten(String longUrl) {
        String hash = hashService.getNextHash();
        Url entity = Url.builder()
                .hash(hash)
                .url(longUrl)
                .createdAt(LocalDateTime.now())
                .lastGetAt(LocalDateTime.now())
                .build();
        urlRepository.save(entity);
        urlCacheRepository.put(hash, longUrl);
        log.info("Shortened URL: {} -> {}", longUrl, hash);
        return hash;
    }

    public String resolve(String hash) {
        String cached = urlCacheRepository.get(hash);
        if (cached != null) return cached;

        return urlRepository.findByHash(hash)
                .map(url -> {
                    urlCacheRepository.put(hash, url.getUrl());
                    return url.getUrl();
                })
                .orElseThrow(() -> new NoSuchElementException("URL not found"));
    }
}