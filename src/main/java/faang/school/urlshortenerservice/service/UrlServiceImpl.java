package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.UrlServiceProperties;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheService urlCacheService;
    private final LocalCache localCache;
    private final UrlServiceProperties properties;

    @Override
    @Transactional
    public ShortUrlDto createShortUrl(UrlDto urlDto) {
        String hash = localCache.getHash();
        urlRepository.save(new Url(hash, urlDto.url(), LocalDateTime.now()));
        urlCacheService.saveUrl(hash, urlDto.url());
        return new ShortUrlDto(properties.getBaseShortUrl() + hash);
    }

    @Override
    public UrlDto getUrl(String hash) {
        String url = urlCacheService.getUrl(hash);
        if (url != null) {
            return new UrlDto(url);
        }
        Url urlEntity = urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Url for hash %s not found", hash)));
        return new UrlDto(urlEntity.getUrl());
    }
}

