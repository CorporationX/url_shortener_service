package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.exception.NoAvailableHashException;
import faang.school.urlshortenerservice.exception.UrlAlreadyExistsException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.properties.UrlValidationProperties;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.core.instrument.Timer;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.event.ExecutionAttemptedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCacheService hashCacheService;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final MetricsService metricsService;
    private final UrlValidationProperties urlValidationProperties;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${url.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${url.retry.delay-ms:100}")
    private long retryDelayMs;

    public String createShortUrl(String longUrl) {
        log.info("Creating short URL for: {}", longUrl);
        metricsService.urlCreationCounter().increment();

        Timer.Sample sample = Timer.start();
        try {

            validateRawUrl(longUrl);

            String normalizedUrl = normalizeUrl(longUrl);

            validateNormalizedUrl(normalizedUrl);

            RetryPolicy<String> retryPolicy = new RetryPolicy<String>()
                    .handle(DataIntegrityViolationException.class)
                    .withDelay(Duration.ofMillis(retryDelayMs))
                    .withMaxRetries(maxRetryAttempts - 1)
                    .onRetry(e -> {
                        log.debug("Retrying URL creation due to conflict, attempt: {}", e.getAttemptCount());
                        metricsService.urlConflictCounter("hash").increment();
                    });

            return Failsafe.with(retryPolicy).get(() -> {
                String cachedHash = urlCacheRepository.getHashByUrl(normalizedUrl);
                if (cachedHash != null) {
                    log.info("Found cached hash for URL: {}", normalizedUrl);
                    metricsService.urlCacheHitCounter().increment();
                    trySaveToCache(cachedHash, normalizedUrl);  // Обновляем TTL
                    return buildShortUrl(cachedHash);
                }

                String existingHash = urlRepository.findByUrl(normalizedUrl);
                if (existingHash != null) {
                    log.info("URL already exists in DB, returning existing hash: {}", existingHash);
                    trySaveToCache(existingHash, normalizedUrl);
                    return buildShortUrl(existingHash);
                }

                String hash = hashCacheService.getHash();
                if (hash == null || hash.isEmpty()) {
                    throw new NoAvailableHashException("Failed to get hash from cache");
                }

                boolean saved = urlRepository.save(hash, normalizedUrl);
                
                if (!saved) {
                    metricsService.urlConflictCounter("url").increment();
                    hashCacheService.returnHash(hash);
                    
                    existingHash = urlRepository.findByUrl(normalizedUrl);
                    if (existingHash != null) {
                        log.info("URL was created by another thread, returning existing hash: {}", existingHash);
                        trySaveToCache(existingHash, normalizedUrl);
                        return buildShortUrl(existingHash);
                    }

                    throw new DataIntegrityViolationException("Save failed, retrying");
                }

                trySaveToCache(hash, normalizedUrl);
                
                String shortUrl = buildShortUrl(hash);
                log.info("Created short URL: {} for long URL: {}", shortUrl, normalizedUrl);
                metricsService.urlCreationSuccessCounter().increment();
                return shortUrl;
            });
        } catch (InvalidUrlException e) {
            metricsService.urlValidationFailureCounter("invalid_url").increment();
            metricsService.urlCreationFailureCounter("validation_error").increment();
            throw e;
        } catch (NoAvailableHashException e) {
            metricsService.urlCreationFailureCounter("no_hash_available").increment();
            throw e;
        } catch (Exception e) {
            metricsService.urlCreationFailureCounter("unknown_error").increment();
            throw e;
        } finally {
            sample.stop(metricsService.urlCreationTimer());
        }
    }

    private void trySaveToCache(String hash, String url) {
        try {
            urlCacheRepository.save(hash, url, 24, java.util.concurrent.TimeUnit.HOURS);
            log.debug("Cached URL mapping: hash={}, url={}", hash, url);
        } catch (Exception e) {
            log.warn("Failed to save to Redis cache (non-critical): hash={}, error={}", hash, e.getMessage());
        }
    }

    private void validateRawUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new InvalidUrlException("URL cannot be null or empty");
        }

        String trimmed = url.trim();
        
        if (trimmed.length() > urlValidationProperties.getMaxLength()) {
            throw new InvalidUrlException("URL exceeds maximum length of " + urlValidationProperties.getMaxLength() + " characters");
        }

        String lowerCase = trimmed.toLowerCase();

        for (String scheme : urlValidationProperties.getForbiddenSchemes()) {
            if (lowerCase.startsWith(scheme.toLowerCase() + ":")) {
                throw new InvalidUrlException("URL scheme '" + scheme + "' is not allowed");
            }
        }

        if (trimmed.startsWith("//")) {
            throw new InvalidUrlException("Protocol-relative URLs are not allowed");
        }
    }

    private void validateNormalizedUrl(String url) {
        try {
            URI uri = new URI(url);
            
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                throw new InvalidUrlException("URL must have http or https scheme");
            }

            String host = uri.getHost();
            if (host == null || host.isEmpty()) {
                throw new InvalidUrlException("URL must have a valid host");
            }

            if (host.contains("..") || host.contains("//")) {
                throw new InvalidUrlException("Invalid host format");
            }

            validateNotPrivate(host);

            if (uri.getRawPath() != null && uri.getRawPath().length() > urlValidationProperties.getMaxLength()) {
                throw new InvalidUrlException("URL path exceeds maximum length");
            }

        } catch (URISyntaxException e) {
            throw new InvalidUrlException("Invalid URL format: " + e.getMessage(), e);
        }
    }

    private void validateNotPrivate(String host) {
        if ("localhost".equalsIgnoreCase(host) || 
            "127.0.0.1".equals(host) ||
            host.startsWith("192.168.") ||
            host.startsWith("10.") ||
            (host.startsWith("172.") && isPrivate172Range(host))) {
            throw new InvalidUrlException("Private IPs and localhost are not allowed");
        }
    }

    private boolean isPrivate172Range(String host) {
        try {
            String[] parts = host.split("\\.");
            if (parts.length >= 2) {
                int secondOctet = Integer.parseInt(parts[1]);
                return secondOctet >= 16 && secondOctet <= 31;
            }
        } catch (NumberFormatException e) {
            // Not an IP address, ignore
        }
        return false;
    }

    private String normalizeUrl(String url) {
        String normalized = url.trim();

        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            if (normalized.contains("://")) {
                throw new InvalidUrlException("Only http and https schemes are allowed");
            }
            normalized = "https://" + normalized;
        }

        try {
            URI uri = new URI(normalized);
            
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                throw new InvalidUrlException("URL must have http or https scheme");
            }

            String host = uri.getHost();
            if (host == null || host.isEmpty()) {
                throw new InvalidUrlException("URL must have a valid host");
            }

            if (host.contains("..") || host.contains("//")) {
                throw new InvalidUrlException("Invalid host format");
            }

            if (uri.getRawPath() != null && uri.getRawPath().length() > urlValidationProperties.getMaxLength()) {
                throw new InvalidUrlException("URL path exceeds maximum length");
            }

        } catch (URISyntaxException e) {
            throw new InvalidUrlException("Invalid URL format: " + e.getMessage(), e);
        }

        return normalized;
    }

    private String buildShortUrl(String hash) {
        String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return base + "/" + hash;
    }

    public String getUrlByHash(String hash) {
        log.debug("Getting URL for hash: {}", hash);
        metricsService.urlRedirectCounter().increment();

        Timer.Sample sample = Timer.start();
        try {
            String url = urlCacheRepository.get(hash);
            if (url != null && !url.isEmpty()) {
                log.debug("URL found in Redis cache for hash: {}", hash);
                metricsService.urlCacheHitCounter().increment();
                metricsService.urlRedirectSuccessCounter().increment();
                return url;
            }

            metricsService.urlCacheMissCounter().increment();
            url = urlRepository.findByHash(hash);
            if (url != null && !url.isEmpty()) {
                log.debug("URL found in database for hash: {}", hash);
                trySaveToCache(hash, url);
                metricsService.urlRedirectSuccessCounter().increment();
                return url;
            }

            log.warn("URL not found for hash: {}", hash);
            metricsService.urlRedirectNotFoundCounter().increment();
            throw new UrlNotFoundException("URL not found for hash: " + hash);
        } finally {
            sample.stop(metricsService.urlRedirectTimer());
        }
    }
}

