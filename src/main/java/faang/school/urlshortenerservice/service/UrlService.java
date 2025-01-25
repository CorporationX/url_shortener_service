package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.config.HashCache;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;

@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;

    @Value("${spring.properties.domain}")
    private String domain;

    @Transactional
//    @CachePut(cacheNames = "redisHashes", key = "#hash")
    public String createShortUrl(UrlDto urlDto) {
        validateUrl(urlDto.getUrl());
        Hash hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash.getHash())
                .url(urlDto.getUrl())
                .createdAt(LocalDateTime.now()).build();
        urlRepository.save(url);
        return domain.concat(hash.getHash());
    }

    public String getUrl(String hash) {
        validateUrl(hash);
        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> new RuntimeException("Hash not found"));
        return url.getUrl();
    }

    private void validateUrl(String url) {
        try {
            new URL(url).toURI();
        } catch (Exception e) {
            log.error("Received a request with invalid url address", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

//    private void addToRedisCache(UrlDto urlDto) {
//        Cache cache = cacheManager.getCache("redisHashes");
//        if (cache != null) {
//            cache.put(urlDto.getHash(), urlDto.getUrl());
//            log.info("Sucessfully added url to redis", urlDto.getUrl());
//        }
//    }
//
//    private String getHashFromRedis(UrlDto urlDto) {
//        Cache cache = cacheManager.getCache("redisHashes");
//        if (cache != null) {
//            return cache.get(urlDto.getHash(), String.class);
//        }
//        log.error("Cannot return the URL from Redis", urlDto.getUrl());
//        return null;
//    }
}
