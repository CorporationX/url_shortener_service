package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.response.UrlResponse;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.hash_generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.HashRepositoryImpl;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Value("${url.host}")
    private String host;

    @Override
    public String getUrl(String hash) {
        return urlCacheRepository.findUrlByHash(hash)
                .orElseGet(() -> findUrlByHash(hash));
    }

    @Override
    public UrlResponse save(UrlDto urlDto) {
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .build();
        urlRepository.save(url);
        urlCacheRepository.save(url);
        return new UrlResponse(getShortUrl(hash));
    }

    private String findUrlByHash(String hash) {
        return urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("hash %s not found".formatted(hash)))
                .getUrl();
    }

    private String getShortUrl(String hash) {
       return "%s/%s".formatted(host, hash);
    }
}
