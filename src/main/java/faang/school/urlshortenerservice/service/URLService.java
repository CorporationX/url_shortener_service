package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.URLRequestDto;
import faang.school.urlshortenerservice.entity.URL;
import faang.school.urlshortenerservice.exception.URLNotFoundException;
import faang.school.urlshortenerservice.repository.RedisURLCacheRepository;
import faang.school.urlshortenerservice.repository.URLRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class URLService {
    private final URLRepository urlRepository;
    private final RedisURLCacheRepository redisURLCacheRepository;
    private final LocalHash localHash;

    @Value("${service.base-url}")
    private String baseUrl;

    @Transactional
    public String createShortURL(URLRequestDto request) {
        String hash = localHash.getHash();

        URL urlEntity = URL.builder()
                .hash(hash)
                .originalUrl(request.url())
                .accessCount(0L)
                .build();
        urlRepository.save(urlEntity);

        redisURLCacheRepository.save(hash, request.url());

        String shortUrl = baseUrl + "/" + hash;
        log.info("Created short URL: {} -> {}", shortUrl, request.url());

        return hash;
    }

    @Transactional(readOnly = true)
    public String getOriginalURL(String hash) {
        Optional<String> cached = redisURLCacheRepository.getByHash(hash);
        if (cached.isPresent()) {
            incrementAccessCount(hash);
            return cached.get();
        }

        Optional<URL> urlOpt = urlRepository.findByHash(hash);
        if (urlOpt.isEmpty()) {
            throw new URLNotFoundException("URL not found for hash: " + hash);
        }

        URL url = urlOpt.get();

        redisURLCacheRepository.save(hash, url.getOriginalUrl());

        incrementAccessCount(hash);

        return url.getOriginalUrl();
    }

    private void incrementAccessCount(String hash) {
        try {
            Optional<URL> urlOpt = urlRepository.findByHash(hash);
            if (urlOpt.isPresent()) {
                URL url = urlOpt.get();
                url.setAccessCount(url.getAccessCount() + 1);
                url.setLastAccessedAt(LocalDateTime.now());
                urlRepository.save(url);
            }
        } catch (Exception e) {
            log.warn("Failed to increment access count for hash: {}", hash, e);
        }
    }
}
