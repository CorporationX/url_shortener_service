package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.domain.DomainConfig;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.dto.ShortenUrlRequest;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.jpa.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static faang.school.urlshortenerservice.config.cache.CacheConfig.URL_CACHE_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashCache hashCache;
    private final DomainConfig domainConfig;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final UrlCacheRepository urlCacheRepository;



    public String shortenUrl(ShortenUrlRequest request) {
        log.info("Shortening URL: {}", request.url());
        String hash = hashCache.getHash();
        String shortenUrl = String.format("%s/%s", domainConfig.getBaseUrl(), hash);

        Url url = urlMapper.toUrl(request);
        url.setHash(hash);

        urlRepository.save(url);
        urlCacheRepository.cacheUrl(hash, request.url());

        log.info("Shortened URL: {} -> {}", request.url(), shortenUrl);

        return shortenUrl;
    }

    @Cacheable(value = URL_CACHE_NAME, key = "#hash")
    public String getOriginalUrl(String hash) {
        return urlRepository.findByHash(hash)
                .orElseThrow(EntityNotFoundException::new)
                .getUrl();
    }

    @Transactional
    public List<Url> deleteExpiredUrls(int batchSize) {
        return urlRepository.deleteExpiredUrls(batchSize);
    }
}
