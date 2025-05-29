package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.properties.UrlProperties;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashService hashService;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlProperties urlProperties;

    @Transactional
    public String shorten(String url) {
        String hash = hashService.getNextHash();
        Url entity = Url.builder()
                .hash(hash)
                .url(url)
                .createdAt(LocalDateTime.now())
                .lastGetAt(LocalDateTime.now())
                .build();
        urlRepository.save(entity);
        urlCacheRepository.put(hash, url);
        log.info("Shortened URL: {} -> {}", url, hash);
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
                .orElseThrow(() -> new EntityNotFoundException("URL not found"));
    }

    @Transactional
    public List<String> deleteExpiredUrls() {
        String interval = toIntervalString(urlProperties.getRetentionPeriod());
        List<String> hashes = urlRepository.deleteOldAndReturnHashes(interval);
        log.info("Deleted {} expired URLs", hashes.size());
        return hashes;
    }

    private String toIntervalString(Duration duration) {
        long days = duration.toDays();
        return days + " days";
    }
}