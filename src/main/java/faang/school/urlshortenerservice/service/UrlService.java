package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String getLongUrl(String hash) {
        log.info("start getLongUrl with hash: {}", hash);

        Url url = urlCacheRepository.findUrlByHash(hash)
                .orElseGet(() -> urlRepository.findUrlByHash(hash)
                        .orElseThrow(() -> new EntityNotFoundException("Long url for: " + hash + " - does not exist")));

        log.info("finish getLongUrl with longUrl: {}", url.getUrl());
        return url.getUrl();
    }
}
