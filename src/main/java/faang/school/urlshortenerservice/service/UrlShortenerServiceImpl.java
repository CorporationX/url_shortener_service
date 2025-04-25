package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.excecption.InvalidUrlException;
import faang.school.urlshortenerservice.excecption.OriginalUrlNotFoundException;
import faang.school.urlshortenerservice.repository.ShortUrlRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.sqids.Sqids;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import static faang.school.urlshortenerservice.messages.ErrorMessages.INVALID_URL;
import static faang.school.urlshortenerservice.messages.ErrorMessages.ORIGINAL_URL_NOT_FOUND;
import static faang.school.urlshortenerservice.messages.ErrorMessages.URL_CAN_T_BE_NULL;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlShortenerServiceImpl implements UrlShortenerService {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final Sqids BASE62_SQIDS = Sqids.builder().alphabet(BASE62).build();

    private final CounterService counterService;
    private final ShortUrlRepository shortUrlRepository;
    private final UrlShortenerRedisService urlShortenerRedisService;
    private final ReentrantLock lock = new ReentrantLock();
    private volatile AtomicLong counter = new AtomicLong(0);

    @Value("${app.short-url-prefix}")
    private String shortUrlPrefix;

    @Value("${app.counter-bach-size}")
    private int counterBatchSize;

    @PostConstruct
    void init() {
        counter.set(counterService.incrementAndGet());
    }

    @Override
    public String createShortUrl(String originalUrl) {
        validateUrl(originalUrl);
        log.info("Received request to create shortened URL from original URL {}", originalUrl);
        String cachedHash = urlShortenerRedisService.getUrlHash(originalUrl);
        if (cachedHash != null) {
            log.info("Found cached shortened URL for {}: {}", originalUrl, shortUrlPrefix + cachedHash);
            return shortUrlPrefix + cachedHash;
        }
        lock.lock();
        String hash;
        try {
            if (counter.get() % counterBatchSize == 0) {
                counter.set(counterService.incrementAndGet());
            }
            hash = toBase62(counter.incrementAndGet());
        } finally {
            lock.unlock();
        }

        String shortUrl = shortUrlPrefix + hash;
        Url url = Url.builder()
                .hash(hash)
                .originalUrl(originalUrl)
                .shortUrl(shortUrl)
                .build();
        try {
            shortUrlRepository.save(url);
            urlShortenerRedisService.addUrlHash(originalUrl, hash);
        } catch (Exception e) {
            log.warn("Error saving shortened URL for original URL {}. Such URL already exists", originalUrl);
        }
        log.info("Created shortened URL for {}: {}", originalUrl, shortUrl);
        return shortUrl;
    }

    @Override
    public String getOriginalUrl(String shortUrl) {
        validateUrl(shortUrl);
        log.info("Received request to get original URL from short URL {}", shortUrl);
        String cachedOriginalUrl = urlShortenerRedisService.getOriginalUrl(shortUrl);
        if (cachedOriginalUrl != null) {
            log.info("Found cached original URL for {}: {}", shortUrl, cachedOriginalUrl);
            return cachedOriginalUrl;
        }

        String originalUrl = shortUrlRepository.findOriginalUrlByShortUrl(shortUrl);
        if (originalUrl == null) {
            log.error(ORIGINAL_URL_NOT_FOUND + shortUrl);
            throw new OriginalUrlNotFoundException(ORIGINAL_URL_NOT_FOUND + shortUrl);
        }
        urlShortenerRedisService.addOriginalUrl(originalUrl, shortUrl);
        log.info("Original URL for {} found in repository: {}", shortUrl, originalUrl);
        return originalUrl;
    }

    private String toBase62(long value) {
        return BASE62_SQIDS.encode(List.of(value));
    }

    private void validateUrl(String url) {
        if (url == null) {
            throw new InvalidUrlException(URL_CAN_T_BE_NULL);
        }
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            log.error(INVALID_URL + url);
            throw new InvalidUrlException(INVALID_URL + url);
        }
    }
}
