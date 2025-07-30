package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.cache.CacheProperties;
import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.common.RecordNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final CacheProperties cacheProperties;

    private final static long URL_EXISTENCE_TIME = 5L;

    @CachePut(cacheManager = "cacheManager",
            cacheNames = "hash",
            key = "#result"
    )
    @Transactional
    public String createShortUrl(UrlRequest urlRequest) {
        String hash = hashCache.takeHash();
        Url url = Url.builder()
                .hash(hash)
                .url(urlRequest.getUrl())
                .expiresAt(LocalDateTime.now().plusMinutes(URL_EXISTENCE_TIME))
                .build();
        urlRepository.save(url);
        return hash;
    }

    @Cacheable(cacheManager = "cacheManager",
            cacheNames = "hash",
            key = "#hash"
    )
    @Transactional(readOnly = true)
    public String getShortUrl(String hash) {
        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> {
                    log.error(hash + " not found");
                    return new RecordNotFoundException("url not found");
                });
        return url.getUrl();
    }

    @Transactional
    public List<Url> getExpiredUrlLocked() {
        return urlRepository.getExpiredUrlsLocked();
    }
}
