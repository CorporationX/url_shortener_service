package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;

    @Transactional
    public UrlDto createShortLink(UrlDto urlDto) {
        String shortLink = hashCache.getHash();
        Url newUrl = new Url();
        newUrl.setUrl(urlDto.getUrl());
        newUrl.setHash(shortLink);
        urlRepository.save(newUrl);
        urlCacheRepository.save(shortLink, urlDto.getUrl());
        log.info("New url added: {}", newUrl);
        return urlMapper.toDto(newUrl);
    }

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        String cacheUrl = urlCacheRepository.get(hash);
        if (cacheUrl != null) {
            log.info("Cache url found: {}", cacheUrl);
            return cacheUrl;
        }
        Url url = urlRepository.findByHash(hash);
        if (url != null) {
            log.info("Url found: {}", url);
            return url.getUrl();
        }
        throw new EntityNotFoundException("URL not found for hash: " + hash);
    }
}
