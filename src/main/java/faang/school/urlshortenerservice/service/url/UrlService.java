package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.config.properties.redis.RedisProperties;
import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.model.url.Url;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final RedisProperties redisProperties;

    @Transactional
    public String saveAndConvertLongUrl(LongUrlDto longUrlDto) {
        String hash = hashCache.getOneHash();
        String longUrl = longUrlDto.getUrl();
        Url urlToConvert = buildUrl(longUrl, hash);
        log.debug("Url to save with url {} and hash {}", longUrl, hash);
        urlCacheRepository.save(hash, longUrl, redisProperties.getTtl());
        urlRepository.save(urlToConvert);
        log.debug("Successfully saved url in redis and db!");
        return hash;
    }

    @Transactional(readOnly = true)
    public Optional<String> retrieveLongUrl(String shortUrl) {
        String hash = extractHash(shortUrl);
        log.debug("Extracted hash {} from incoming shortUrl, searching for long url", hash);
        return urlCacheRepository.find(hash)
                .or(() -> urlRepository.findUrlByHash(hash))
                .orElseThrow(() -> new EntityNotFoundException("URL with hash " + hash + " not found!"))
                .describeConstable();
    }

    private Url buildUrl(String longUrl, String hash) {
        return Url.builder()
                .hash(hash)
                .url(longUrl)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
