package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.AppProperties;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.hash.LocalCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final LocalCache localCache;
    private final AppProperties appProperties;

    public String createShortLink(UrlDto urlDto) {
        String hash = localCache.getHash();
        String url = urlDto.url();

        urlRepository.save(hash, url);
        urlCacheRepository.save(hash, url);

        log.info("Hash {} for URL {} has been created", hash, url);
        return formShortUrl(hash);
    }

    public String getUrl(String hash) {
        return urlCacheRepository.get(hash)
                .map(url -> {
                    log.info("URL {} obtained from Redis", url);
                    return url;
                })
                .orElseGet(() -> {
                    try {
                        String url = urlRepository.get(hash);
                        log.info("URL {} obtained from database", url);
                        return url;
                    } catch (EmptyResultDataAccessException ex) {
                        log.warn("URL not found for hash: {}", hash);
                        throw new UrlNotFoundException(formShortUrl(hash));
                    }
                });
    }

    private String formShortUrl(String hash) {
        return appProperties.baseUrl() + hash;
    }
}
