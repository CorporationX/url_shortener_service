package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.properties.UrlLifeTimeConfig;
import faang.school.urlshortenerservice.exception.DuplicateUrlException;
import faang.school.urlshortenerservice.exception.UrlExpiredException;
import faang.school.urlshortenerservice.model.dto.UrlRequestDto;
import faang.school.urlshortenerservice.model.dto.UrlResponseDto;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.producer.RabbitQueueProducerService;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlLifeTimeConfig lifeTime;
    private final RabbitQueueProducerService rabbitQueueProducerService;

    @Value("${app.short-url-prefix}")
    private String pathWithHashedUrl;

    @Transactional
    public UrlResponseDto generateShortUrl(UrlRequestDto requestDto) {
        urlRepository.findByUrl(requestDto.getLongUrl())
                .ifPresent(existingUrl -> {
                    throw new DuplicateUrlException(requestDto.getLongUrl());
                });

        String hash = hashCache.getHash();
        LocalDateTime expirationTime = calculateExpirationTime();
        Url url = createUrl(requestDto.getLongUrl(), hash, expirationTime);

        saveUrl(url);
        rabbitQueueProducerService.sendUrlIdForValidation(url.getHash());

        return buildResponse(hash);
    }

    public String getUrlByHash(String hash) {
        Optional<Url> urlOpt = urlCacheRepository.findByHash(hash);
        return urlOpt.map(Url::getUrl)
                .orElseGet(() -> urlRepository.findByHash(hash)
                        .map(Url::getUrl)
                        .orElseThrow(() -> new UrlExpiredException(buildShortUrl(hash))));
    }

    @Transactional
    public List<String> cleanHashes() {
        return urlRepository.getOldUrlsAndDelete();
    }

    private LocalDateTime calculateExpirationTime() {
        return LocalDateTime.now()
                .plusMonths(lifeTime.getMonths())
                .plusDays(lifeTime.getDays())
                .plusHours(lifeTime.getHours());
    }

    private Url createUrl(String longUrl, String hash, LocalDateTime expirationTime) {
        return Url.builder()
                .hash(hash)
                .url(longUrl)
                .expirationTime(expirationTime)
                .build();
    }

    private void saveUrl(Url url) {
        urlRepository.save(url);
        urlCacheRepository.save(url);
    }

    private UrlResponseDto buildResponse(String hash) {
        return UrlResponseDto.builder()
                .shortUrl(buildShortUrl(hash))
                .build();
    }

    private String buildShortUrl(String hash) {
        return pathWithHashedUrl + hash;
    }
}
