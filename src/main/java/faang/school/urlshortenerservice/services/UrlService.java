package faang.school.urlshortenerservice.services;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.entities.Url;
import faang.school.urlshortenerservice.repositories.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final LocalHashService localHashService;
    private final UrlRepository urlRepository;
    private final UrlCacheService cacheService;
    
    @Value("${url-shortener.base-url}")
    private String baseUrl;

    public String createHashUrl(HashDto dto) {
        String hash = localHashService.getHash();
        Url url = Url.builder()
                .url(dto.getUrl())
                .hash(hash)
                .build();
        urlRepository.save(url);
        
        // Кешируем URL после сохранения
        cacheService.cacheUrl(hash, dto.getUrl());

        return baseUrl + hash;
    }

    public String getOriginalUrl(String hash) {
        String cachedUrl = cacheService.getCachedUrl(hash);
        if (cachedUrl != null) {
            cacheService.incrementClickCount(hash);
            return cachedUrl;
        }

        String originalUrl = urlRepository.findUrlByHash(hash);
        if (originalUrl != null) {
            cacheService.cacheUrl(hash, originalUrl);
            cacheService.incrementClickCount(hash);
        }
        
        return originalUrl;
    }
}

