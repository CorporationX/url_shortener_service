package faang.school.urlshortenerservice.service.url_shortener;

import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.repository.url.impl.UrlRepositoryImpl;
import faang.school.urlshortenerservice.repository.url_cash.impl.UrlCacheRepositoryImpl;
import faang.school.urlshortenerservice.service.hash_cashe.HashCache;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepositoryImpl urlCacheRepository;
    private final UrlRepositoryImpl urlRepository;
    private final HashCache hashCache;

    @Value("${url.base-pattern}")
    private String domain;

    @Transactional
    public String shortenUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        urlRepository.save(hash, urlDto.getUrl());
        log.info("Hash - {}, Url - {} was saved to Url repository", hash, urlDto.getUrl());

        urlCacheRepository.saveUrl(hash, urlDto.getUrl());
        log.info("Hash - {}, Url - {} was saved to UrlCache repository", hash, urlDto.getUrl());

        String shortenedUrl = domain + hash;
        log.info("Shortened Url - {}, was created", shortenedUrl);
        return shortenedUrl;
    }

    @Transactional
    public String getOriginalUrl(String hash) {
        String originalUrl = urlCacheRepository.getUrl(hash);

        if (originalUrl == null) {
            originalUrl = urlRepository.findLongUrlByHash(hash).
                    orElseThrow(() -> new EntityNotFoundException("No url found for hash: " + hash));
            urlCacheRepository.saveUrl(hash, originalUrl);
            log.info("Hash - {}, Url - {} was saved to UrlCache repository", hash, originalUrl);
        }

        log.info("Got original Url - {} related to Hash - {}", originalUrl, hash);
        return originalUrl;
    }
}
