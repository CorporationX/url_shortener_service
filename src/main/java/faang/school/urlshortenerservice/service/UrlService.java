package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@CacheConfig(cacheNames = "urls")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${server.base-url}")
    private String baseUrl;

    @Cacheable
    public String getOriginalUrl(String hash) {
        Url url = urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("Url not found"));
        return url.getUrl();
    }

    @Transactional
    public UrlDto generateShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        Url url = new Url();
        url.setHash(hash);
        url.setUrl(urlDto.url());
        urlRepository.save(url);
        urlCacheRepository.save(hash, urlDto.url());
        String shortUrl = baseUrl + "/" + hash;
        return new UrlDto(shortUrl);
    }
}