package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@CacheConfig
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    @Value("${params.url-service.url-pattern}")
    private String urlPattern;

    private final HashCache hashCache;
    private final UrlRepository urlRepository;

    @Override
    @Cacheable(value = "hashCache", key = "#hash")
    public String getUrl(String hash) {
        Url url = urlRepository.findByHash(hash).orElseThrow(() -> {
            String message = "No url was found for this hash: %s".formatted(hash);
            return new EntityNotFoundException(message);
        });
        log.info("Found URL: {}", url.getUrl());
        return url.getUrl();
    }

    @Override
    @Cacheable(value = "urlCache", key = "#urlDto.url()")
    @Transactional
    public String getShortUrl(UrlDto urlDto) {
        String hash = urlRepository.findByUrl(urlDto.url())
                .orElseGet(() -> createNewUrl(urlDto));
        return urlPattern + hash;
    }

    private String createNewUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .url(urlDto.url())
                .hash(hash)
                .created_at(LocalDateTime.now())
                .build();
        urlRepository.save(url);
        log.info("Url has been generated: {}", url);
        return hash;
    }
}
