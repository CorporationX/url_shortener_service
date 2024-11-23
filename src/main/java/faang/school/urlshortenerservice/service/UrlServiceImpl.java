package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.RedisCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final RedisCache redisCache;
    private final HashCache hashCache;

    @Value("${spring.interval-hours}")
    private Long urlLifeTime;

    @Override
    public String getLongUrlByHash(String hash) {
        return redisCache.getFromCache(hash)
                .or(() -> urlRepository.findUrlByHash(hash))
                .orElseThrow(() -> new UrlNotFoundException("URL not found for hash %s".formatted(hash)));
    }

    @Override
    public String getShortUrlByHash(String url) {
        String hashFromCache = hashCache.getHash();

        redisCache.saveToCache(hashFromCache, url);
        urlRepository.save(Url.builder()
                .hash(hashFromCache)
                .url(url)
                .build());

        return url;
    }

    @Override
    @Transactional
    public void cleaningOldHashes() {
        LocalDateTime dateTime = LocalDateTime.now().minusHours(urlLifeTime);
        hashRepository.saveAll(urlRepository.deleteOldUrlsAndReturnHashes(dateTime));
    }
}