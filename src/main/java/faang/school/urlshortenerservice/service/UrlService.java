package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.UrlRedis;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String getLongUrl(String shortUrl) {
        Optional<String> url = getUrlFromCacheOrDb(shortUrl);
        return url.orElseThrow(() -> new UrlNotFoundException("Url not found"));
    }

    private Optional<String> getUrlFromCacheOrDb(String shortUrl) {
        Optional<UrlRedis> urlCashRedis = urlCacheRepository.findById(shortUrl);
        if (urlCashRedis.isPresent()) {
            log.info("Found url in cache: {}", urlCashRedis.get().getUrl());
            return Optional.of(urlCashRedis.get().getUrl());
        } else {
            String urlBd = urlRepository.findUrlByHash(shortUrl);
            if (urlBd != null) {
                UrlRedis urlRedis = UrlRedis.builder().id(shortUrl).url(urlBd).build();
                urlCacheRepository.save(urlRedis);
                log.info("Saved url in cache: {}", urlRedis);
                return Optional.of(urlBd);
            }
        }
        return Optional.empty();
    }
}
