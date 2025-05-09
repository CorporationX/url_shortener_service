package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.excecption.InvalidUrlException;
import faang.school.urlshortenerservice.excecption.OriginalUrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
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
    private final UrlRepository urlRepository;
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicLong counter = new AtomicLong(0);

    @Value("${app.short-url-prefix}")
    private String shortUrlPrefix;

    @Value("${app.counter-bach-size}")
    private int counterBatchSize;

    @PostConstruct
    void init() {
        counter.set(counterService.incrementAndGet());
    }

    @Override
    @Cacheable(value = "${app.original-url-key}", key = "#originalUrl")
    public String createShortUrl(String originalUrl) {
        validateUrl(originalUrl);
        lock.lock();
        long value;
        try {
            if (counter.get() % counterBatchSize == 0) {
                counter.set(counterService.incrementAndGet());
            }
            value = counter.incrementAndGet();
        } finally {
            lock.unlock();
        }
        String hash = toBase62(value);
        Url url = Url.builder()
                .hash(hash)
                .originalUrl(originalUrl)
                .build();

        String shortUrl = shortUrlPrefix + hash;
        try {
            urlRepository.save(url);
            log.info("Created shortened URL for {}: {}", originalUrl, shortUrl);
        } catch (Exception e) {
            log.warn("Shortened URL for {} already exists in the database", originalUrl);
        }
        return shortUrl;
    }

    @Override
    @Cacheable(value = "${app.hash-url-key}", key = "#shortUrl")
    public String getOriginalUrl(String shortUrl) {
        validateUrl(shortUrl);
        String hash = shortUrl.substring(shortUrl.lastIndexOf('/') + 1);
        String originalUrl = urlRepository.findOriginalUrlByHash(hash);
        if (originalUrl == null) {
            log.error(ORIGINAL_URL_NOT_FOUND + shortUrl);
            throw new OriginalUrlNotFoundException(ORIGINAL_URL_NOT_FOUND + shortUrl);
        }
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
