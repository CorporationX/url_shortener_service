package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.CustomUrl;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final HashCache hashCache;

    @Value("${redis-time-to-live-seconds.new-url}")
    private long timeToLiveSecondsForNew;
    @Value("${redis-time-to-live-seconds.requested-url}")
    private long timeToLiveSecondsForRequested;
    @Value("${url-data.protocol}")
    private String protocol;
    @Value("${url-data.domain}")
    private String domain;
    @Value("${server.port}")
    private int port;

    @Transactional
    public URL convertToShortUrl(URL url) {
        log.info("Converting url: {}", url);
        String hash = hashCache.getHash();
        CustomUrl customUrl = CustomUrl.builder()
                .hash(hash)
                .url(url.toString())
                .build();
        urlRepository.save(customUrl);
        saveToRedis(url.toString(), hash, timeToLiveSecondsForNew);
        log.info("Converted url: {}, hash: {}", url, hash);
        try {
            return new URL(protocol, domain, port, "/" + hash);
        } catch (MalformedURLException e) {
            log.error("Malformed URL", e);
            throw new RuntimeException("Malformed URL", e);
        }
    }

    @Transactional
    public String getFullUrl(String hash) {
        log.info("Fetching full url for hash: {}", hash);
        String url = redisTemplate.opsForValue().get(hash);
        if (url == null) {
            url = urlRepository.findUrlByHash((hash));
            saveToRedis(url, hash, timeToLiveSecondsForRequested);
        }
        if (url == null) {
            log.error("No url found for hash: {}", hash);
            throw new UrlNotFoundException("No url found for hash: " + hash);
        }
        log.info("Returning full url {} for hash: {}", url, hash);
        return url;
    }

    private void saveToRedis(String url, String hash, long timeToLiveSeconds) {
        redisTemplate.opsForValue().set(hash, url, timeToLiveSeconds, TimeUnit.SECONDS);
    }
}
