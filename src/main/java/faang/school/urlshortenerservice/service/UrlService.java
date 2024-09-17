package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public String getFullUrl(String hash) {
        String url = urlCacheRepository.get(hash);
        if (url != null) {
            return url;
        }
        Optional<Url> urlByHash = urlRepository.findById(hash);
        if (urlByHash.isPresent()) {
            return urlByHash.get().getUrl();
        } else {
            log.error("Hash doesn't exist");
            throw new EntityNotFoundException("Hash doesn't exist");
        }
    }

    private String getFullHash(String hash) {
        return String.format("http://%s:%d/api/%s", host, port, hash);
    }
}
