package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    @Value("${server.host}")
    private String host;
    @Value("${server.port}")
    private int port;
    private final HashCache cache;
    private final UrlJpaRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String getShortUrl(UrlDto url) {
        String hash = cache.getHash();
        Url shortUrl = Url.builder()
                .hash(hash)
                .url(url.getUrl())
                .build();
        urlCacheRepository.save(hash, url.getUrl());
        urlRepository.save(shortUrl);
        log.info("Short url was successfully created: {}", shortUrl);
        return getFullHash(hash);
    }

    private String getFullHash(String hash) {
        return String.format("https://%s:%d/api/%s", host, port, hash);
    }
}
