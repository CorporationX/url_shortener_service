package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Transactional
    public String shortenUrl(UrlDto urlDto) {
        String existingHash = urlRepository.findHashByUrl(urlDto.getUrl());
        if (existingHash != null) {
            return String.format("%s/%s", "http://localhost:8080", existingHash);
        }

        String hash = hashCache.getHash();

        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .createdAt(LocalDateTime.now())
                .build();

        urlRepository.save(url);
        urlCacheRepository.save(hash, url.getUrl());

        return String.format("%s/%s", "http://localhost:8080", hash);
    }

    public String getOriginalUrl(String hash) {
        String originalUrl = urlCacheRepository.getCacheValue(hash);
        if (originalUrl != null) {
            return originalUrl;
        }

        return urlRepository.findById(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new RuntimeException("URL not found for hash: " + hash));
    }
}
