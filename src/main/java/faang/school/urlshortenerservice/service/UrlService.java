package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final RedisHashPoolService hashPool;

    @Value("${ttl.hour.url}")
    private int ttlHours;

    @Value("${retry.max_attempts}")
    private int maxAttempts;

    @Value("${retry.initial_delay_ms}")
    private int delayMs;

    @Value("${retry.backoff_multiplier}")
    private double backoffMultiplier;

    public String createShortUrl(String longUrl) {
        String hash = hashPool.acquire();
        saveUrl(hash, longUrl);
        return hash;
    }

    @Retryable(
            value = { DataIntegrityViolationException.class },
            maxAttemptsExpression = "#{@urlService.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "#{@urlService.delayMs}",
                    multiplierExpression = "#{@urlService.backoffMultiplier}"
            )
    )
    @Transactional
    public void saveUrl(String hash, String longUrl) {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(ttlHours);
        urlRepository.save(new Url(hash, longUrl, expiresAt));
        urlCacheRepository.save(hash, longUrl);
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        return urlCacheRepository.findByHash(hash)
                .or(() -> urlRepository.findByHash(hash).map(url -> {
                    urlCacheRepository.save(url.getHash(), url.getUrl());
                    return url.getUrl();
                }))
                .orElseThrow(() -> new ResourceNotFoundException("URL not found for hash: " + hash));
    }
}