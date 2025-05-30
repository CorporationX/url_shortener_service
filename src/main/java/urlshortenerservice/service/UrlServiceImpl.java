package urlshortenerservice.service;

import urlshortenerservice.cache.HashCache;
import urlshortenerservice.dto.UrlResponseDto;
import urlshortenerservice.entity.Url;
import urlshortenerservice.exception.InvalidUrlException;
import urlshortenerservice.exception.UrlNotFoundException;
import urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;

import static urlshortenerservice.message.ErrorMessage.INVALID_URL;
import static urlshortenerservice.message.ErrorMessage.URL_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${shortener.base-url}")
    private String baseUrl;

    @Value("${shortener.redis.url-ttl}")
    private int urlTtl;

    @Transactional
    @Override
    public String getOriginalUrl(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        if (url != null) {
            log.info("Url found from redis: {}", url);
            return url;
        }

        return urlRepository.findByHash(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> {
                    log.error(URL_NOT_FOUND);
                    return new UrlNotFoundException(URL_NOT_FOUND);
                });
    }

    @Transactional
    @Override
    public UrlResponseDto createShortUrl(String originalUrl) {
        validateUrl(originalUrl);
        String existingHash = redisTemplate.opsForValue().get(originalUrl);
        if (existingHash != null) {
            return new UrlResponseDto(baseUrl + existingHash);
        }

        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .createdAt(LocalDateTime.now())
                .build();

        urlRepository.save(url);
        redisTemplate.opsForValue().set(hash, url.getUrl(), Duration.ofHours(urlTtl));
        redisTemplate.opsForValue().set(url.getUrl(), hash);
        log.info("Short URL created: {} -> {}", originalUrl, hash);

        return new UrlResponseDto(baseUrl + hash);
    }

    private void validateUrl(String originalUrl) {
        try {
            new URL(originalUrl).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            log.error(INVALID_URL);
            throw new InvalidUrlException(INVALID_URL);
        }
    }
}
