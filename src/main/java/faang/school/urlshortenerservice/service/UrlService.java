package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;

    private static final String BASE_URL = "https://url-shortener.faang.org/";

    public String getOriginalUrl(String hash) {
        String cachedUrl = urlCacheRepository.find(hash);
        if (cachedUrl != null) {
            log.info("Cache hit for hash: {}", hash);
            return cachedUrl;
        }

        Url url = urlRepository.findByHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("URL not found"));

        log.info("Cache miss for hash: {}. Saving to cache.", hash);
        urlCacheRepository.save(hash, url.getUrl());
        return url.getUrl();
    }

    @Transactional
    public UrlDto shortenUrl(UrlDto urlDto) {
        String hash = hashCache.getHash().join();

        Url url = Url.builder()
                .url(urlDto.url())
                .hash(hash)
                .createdAt(LocalDateTime.now())
                .build();

        urlRepository.save(url);
        urlCacheRepository.save(hash, urlDto.url());
        log.info("URL shortened: {} -> {}", urlDto.url(), hash);
        return UrlDto.builder().url(BASE_URL + hash).build();
    }
}
