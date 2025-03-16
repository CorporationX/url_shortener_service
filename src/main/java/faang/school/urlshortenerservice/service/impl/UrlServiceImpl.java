package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.LocalHashCache;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final LocalHashCache localHashCache;
    private final UrlMapper urlMapper;
    private final ShortenerProperties shortenerProperties;

    @Transactional
    @CachePut(value = "url", key = "#urlAddress", unless = "#result == null")
    public UrlResponseDto createCachedUrl(String urlAddress) {
        String hash = localHashCache.getFreeHashFromQueue().getHash();
        Url url = Url.builder()
                .url(urlAddress)
                .hash(hash)
                .expiredAtDate(LocalDateTime.now().plusDays(shortenerProperties.url().ttlDays()))
                .build();
        log.info("Url '{}' cached and saved to database", urlAddress);
        return urlMapper.toUrlResponseDto(urlRepository.save(url));
    }

    @Cacheable(value = "url", key = "#urlAddress", unless = "#result == null")
    public UrlResponseDto getUrl(String urlAddress) {
        Url url = urlRepository.findByUrl(urlAddress);
        log.info("Read data from cache/db by url '{}': {}", urlAddress, url);
        UrlResponseDto urlResponseDto;
        if (url != null) {
            urlResponseDto = urlMapper.toUrlResponseDto(url);
            log.info("Try to get url from cache/db. Looking for {}, found {}",
                    urlAddress, urlResponseDto.getShortUrl());
        } else {
            log.info("Empty result got from db, empty dto created");
            urlResponseDto = UrlResponseDto.builder().build();
        }
        return urlResponseDto;
    }

    @Cacheable(value = "redirect_url", key = "#hash")
    public UrlResponseDto getUrlByHash(String hash) {
        return urlMapper.toUrlResponseDto(urlRepository.getReferenceById(hash));
    }
}
