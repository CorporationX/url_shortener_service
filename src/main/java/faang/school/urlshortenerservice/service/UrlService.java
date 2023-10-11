package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.ñache.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    @Transactional
    public String getShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        urlCacheRepository.save(hash, urlDto.getUrl());
        urlRepository.save(hash, urlDto.getUrl());
        return urlDto.getUrl();
    }

    public String getLongUrl(String hash) {
        String cacheUrl = urlCacheRepository.getUrl(hash);
        String urlByHash = urlRepository.findUrlByHash(hash);
        if (cacheUrl == null && urlByHash == null) {
            log.error("Hash doesn't exist");
            throw new NotFoundException("Hash doesn't exist");
        }
        if (cacheUrl != null) {
            return cacheUrl;
        }
        return urlByHash;
    }
}
