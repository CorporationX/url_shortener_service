package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.error.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashLocalCache;
import faang.school.urlshortenerservice.service.UrlCacheService;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final HashLocalCache hashLocalCache;
    private final UrlMapper urlMapper;
    private final ShortenerProperties shortenerProperties;
    private final UrlCacheService urlCacheService;

    @Transactional
    public UrlResponseDto getOrCreateUrl(String urlAddress) {

        Url url = urlCacheService.getUrl(urlAddress);
        if (url != null) {
            log.info("Got url '{}' from cache", urlAddress);
            return urlMapper.toUrlResponseDto(url);
        }

        url = urlRepository.findByUrl(urlAddress);
        if (url != null) {
            log.info("Got url '{}' from database", urlAddress);
            urlCacheService.setUrl(urlAddress, url);
            return urlMapper.toUrlResponseDto(url);
        }

        String hash = hashLocalCache.getFreeHashFromQueue().getHash();
        url = urlRepository.save(new Url(hash, urlAddress,
                LocalDateTime.now().plusDays(shortenerProperties.url().ttlDays())));
        urlCacheService.setUrl(urlAddress, url);
        log.info("Create and get url '{}'", urlAddress);
        return urlMapper.toUrlResponseDto(url);
    }

    @Cacheable(value = "redirect_url", key = "#hash")
    public UrlResponseDto getUrlByHash(String hash) {
        Optional<Url> optionalUrl = urlRepository.findById(hash);
        Url url = optionalUrl.orElseThrow(() -> new UrlNotFoundException("Url not found, hash: " + hash));
        return urlMapper.toUrlResponseDto(url);
    }
}
