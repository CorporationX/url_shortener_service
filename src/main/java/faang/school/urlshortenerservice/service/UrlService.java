package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    @Value("${url.short-template}")
    private String shortUrlTemplate;

    @Value("${url.expiration-days}")
    private int expirationDays;

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashService hashService;

    @Transactional
    public String createShortUrl(String longUrl) {
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(expirationDays);

        String hash = hashService.getFreeHash();
        Url url = new Url(hash, longUrl, expiredAt);

        urlRepository.save(url);
        urlCacheRepository.save(hash, longUrl);

        String shortUrl = UriComponentsBuilder.fromHttpUrl(shortUrlTemplate)
            .path("/")
            .path(hash)
            .build()
            .toUriString();

        log.info("Created short URL: {}", shortUrl);
        return shortUrl;
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        String cachedUrl = urlCacheRepository.findUrlByHash(hash);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        String originalUrl = urlRepository.findUrlByHash(hash)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        urlCacheRepository.save(hash, originalUrl);
        return originalUrl;
    }
}
