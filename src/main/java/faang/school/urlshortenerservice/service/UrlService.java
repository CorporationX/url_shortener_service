package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.jpa.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Validated
@RequiredArgsConstructor
@CacheConfig(cacheNames = "urlCache")
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final CacheManager cacheManager;
    @Value("${app.root_path}")
    private String rootPath;
    @Value("${app.url_retention_period_month:6}")
    private int urlRetentionPeriod;

    @Cacheable(key = "#hash")
    public String getOriginalUrl(
            @Size(
                    min = 4,
                    max = 8,
                    message = "Hash size should be between 4 and 8 characters inclusive"
            )
            String hash) {
        Url url = findUrlByHash(hash);
        return url.getUrl();
    }

    @Transactional
    public UrlDto createShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = validateExpiresAt(urlDto, now);

        Url urlToSave = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();

        Url url = urlRepository.save(urlToSave);
        Objects.requireNonNull(cacheManager.getCache("urlCache")).put(hash, url.getUrl());

        return toDto(url);
    }

    @Transactional
    public List<String> getExpiredHashAndDeleteUrl() {
        return urlRepository.getExpiredHashAndDeleteUrl();
    }

    public Url findUrlByHash(String hash) {
        return urlRepository.findById(hash)
                .orElseThrow(() -> new NotFoundException("Url not found"));
    }

    public LocalDateTime validateExpiresAt(UrlDto dto, LocalDateTime now) {
        if (dto.getExpiresAt() == null || dto.getExpiresAt().isBefore(now)) {
            return now.plusMonths(urlRetentionPeriod);
        }
        return dto.getExpiresAt();
    }

    public UrlDto toDto(Url url) {
        return UrlDto.builder()
                .url(rootPath + url.getHash())
                .expiresAt(url.getExpiresAt())
                .build();
    }
}