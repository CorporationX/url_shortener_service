package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.ShortUrl;
import faang.school.urlshortenerservice.repository.CounterRepository;
import faang.school.urlshortenerservice.repository.ShortUrlRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlShortenerServiceImpl implements UrlShortenerService {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Value("${app.short-url-prefix}")
    private String shortUrlPrefix;

    @Value("${app.counter-bach-size}")
    private int counterBatchSize;

    private final CounterService counterService;
    private final ShortUrlRepository shortUrlRepository;
    private final CounterRepository counterRepository;
    private AtomicLong counter;

    @PostConstruct
    void init() {
        counter = new AtomicLong(counterRepository.getValueForUpdate().getValue());
    }

    @Override
    public String createShortenedUrl(String originalUrl) {
        log.info("Received request to create shortened url from original url {}", originalUrl);
        String hash = toBase62(counter.incrementAndGet());
        ShortUrl shortUrl = ShortUrl.builder()
                .hash(hash)
                .originalUrl(originalUrl)
                .build();

        shortUrlRepository.save(shortUrl);
        String result = shortUrlPrefix + hash;
        log.info("Created shortened url {} for {}", result, originalUrl);
        return result;
    }

    @Override
    public String getOriginalUrl(String shortUrl) {
        log.info("Received request to get original url from short url {}", shortUrl);
        return "";
    }

    private String toBase62(long value) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(BASE62.charAt((int) (value % 62)));
            value /= 62;
        } while (value > 0);
        return sb.reverse().toString();
    }
}
