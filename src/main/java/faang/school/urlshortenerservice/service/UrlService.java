package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Transactional(readOnly = true)
    @Cacheable(value = "urlCache", key = "#hash")
    public String getLongUrlByHash(String hash) {
        String cachedUrl = urlCacheRepository.findByHash(hash).orElse(null);

        if (cachedUrl != null) {
            return cachedUrl;
        }

        Url url = urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException(Url.class, hash));

        urlCacheRepository.save(hash, url.getUrl());

        return url.getUrl();
    }

    @Transactional
    public String createShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash().join();
        saveShortUrl(hash, urlDto);
        return hash;
    }

    private void saveShortUrl(String hash, UrlDto urlDto) {
        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .build();
        urlRepository.save(url);
        urlCacheRepository.save(hash, urlDto.getUrl());
    }
}