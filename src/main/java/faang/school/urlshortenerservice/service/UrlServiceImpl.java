package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
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

import static faang.school.urlshortenerservice.message.ErrorMessage.URL_NOT_CORRECT;
import static faang.school.urlshortenerservice.message.ErrorMessage.URL_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${shortener.base-url}")
    private String baseUrl;

    @Transactional
    @Override
    public String getOriginalUrl(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        if (url != null) {
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
    public UrlResponseDto createUrl(String originalUrl) {
        validateUrl(originalUrl);
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .createdAt(LocalDateTime.now())
                .build();

        urlRepository.save(url);
        redisTemplate.opsForValue().set(hash, url.getUrl(), Duration.ofHours(24));
        log.info("Short URL created: {} -> {}", hash, originalUrl);
        return new UrlResponseDto(baseUrl + hash);
    }

    private void validateUrl(String originalUrl) {
        try {
            new URL(originalUrl).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            log.error(URL_NOT_CORRECT);
            throw new InvalidUrlException(URL_NOT_CORRECT);
        }
    }
}
