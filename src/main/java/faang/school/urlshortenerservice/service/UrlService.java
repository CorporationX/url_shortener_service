package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.cashe.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final HashService hashService;
    private final UrlRepository urlRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.url.name}")
    private String httpsPrefix;
    @Value("${spring.hours}")
    private int hours;

    public String makeShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        String shortUrl;
        if (urlDto.getHour() != 0) {
            shortUrl = httpsPrefix + urlRepository.save(hash, urlDto.getUrl(), urlDto.getHour());
        } else {
            shortUrl = httpsPrefix + urlRepository.save(hash, urlDto.getUrl(), hours);
        }

        try {
            redisTemplate.opsForValue().set(shortUrl, urlDto.getUrl(), Duration.ofHours(urlDto.getHour()));
            log.info("URL added into cache: {}, {}", shortUrl, urlDto.getUrl());
        } catch (Exception e) {
            log.error("Failed to cache in Redis: {}", e.getMessage());
        }
        return shortUrl;
    }

    public String getOriginalUrl(String shortUrl) {
        try {
            String longUrl = (String) redisTemplate.opsForValue().get(shortUrl);
            if (longUrl != null) {
                log.info("URL retrieved from cache: {}, {}", shortUrl, longUrl);
                return longUrl;
            }
        } catch (Exception e) {
            log.error("Failed to retrieve from Redis: {}", e.getMessage());
        }
        return getOriginalUrlFromDatabase(shortUrl);
    }

    private String getOriginalUrlFromDatabase(String shortUrl) {
        String hash = extractHash(shortUrl);
        return urlRepository.getOriginalUrl(hash)
                .orElseThrow(() -> new EntityNotFoundException("No match found for: " + shortUrl));
    }

    private String extractHash(String shortUrl) {
        return shortUrl.substring(httpsPrefix.length());
    }

    public void findAndDeleteExpiredUrls() {
        List<String> hashes = urlRepository.findAndDeleteExpiredUrls();
        hashService.save(hashes);
    }
}
