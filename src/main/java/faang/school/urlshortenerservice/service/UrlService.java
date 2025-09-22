package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlRequest;
import faang.school.urlshortenerservice.entity.ShortUrl;
import faang.school.urlshortenerservice.exception.common.RecordNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validation.UrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private static final long DEFAULT_EXPIRATION_TIME_YEAR_DELTA = 1L;

    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlValidator validator;
    private final RedisUrlCacheService redisUrlCacheService;

    public ShortUrl findByHash(String hash) {
        return urlRepository.findByHash(hash)
                .orElseThrow(() -> new RecordNotFoundException("Invalid short url. Corresponding url not found."));
    }

    public ShortUrl getActualUrl(String hash) {
        Optional<ShortUrl> redisShortUrl = redisUrlCacheService.getUrl(hash);

        if (redisShortUrl.isPresent()) {
            log.debug("Url from cache");
            return redisShortUrl.get();
        }

        ShortUrl shortUrl = findByHash(hash);
        redisUrlCacheService.cacheUrl(shortUrl);

        validator.validateNotExpired(shortUrl);
        log.debug("Url from db");
        return shortUrl;
    }

    public ShortUrl getShortUrl(ShortUrlRequest request) {
        ShortUrl newShortUrl = ShortUrl.builder()
                .hash(hashCache.getHash())
                .actualUrl(request.url())
                .expirationTime(setExpirationTimeOrDefault(request))
                .build();

        newShortUrl = urlRepository.save(newShortUrl);

        redisUrlCacheService.cacheUrl(newShortUrl);

        return newShortUrl;
    }

    @Transactional
    public void deleteExpiredShortUrls(int limit) {
        List<String> expiredHashes = urlRepository.findExpiredUrlsHashes(limit);
        log.info("Batch contains {} hashes.", expiredHashes.size());
        if (!expiredHashes.isEmpty()) {
            urlRepository.deleteAllByIdInBatch(expiredHashes);
            redisUrlCacheService.deleteUrlFromCacheAllIn(expiredHashes);
        }
    }

    private LocalDateTime setExpirationTimeOrDefault(ShortUrlRequest request) {
        LocalDateTime maxExpirationTime = LocalDateTime.now().plusYears(DEFAULT_EXPIRATION_TIME_YEAR_DELTA);
        boolean defaultRequired = request.expirationTime() == null || request.expirationTime().isAfter(maxExpirationTime);
        return defaultRequired ? maxExpirationTime : request.expirationTime();
    }
}