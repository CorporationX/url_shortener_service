package faang.school.urlshortenerservice.service.url_shortener;

import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.repository.url_cash.UrlCacheRepository;
import faang.school.urlshortenerservice.service.hash_cashe.HashCache;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Value("${url.base-pattern}")
    private String domain;

    // create new Docker container for DB and use different port ??
    // (think how add docker to load in this project if possible)
    @Transactional
    public UrlDto shortenUrl(UrlDto longUrlDto) {
        String hash = hashCache.getHash();
        urlRepository.save(hash, longUrlDto.getUrl());
        urlCacheRepository.saveUrl(hash, longUrlDto.getUrl());
        return UrlDto.builder()
                .url(domain + hash)
                .build();
    }

    public String getOriginalUrl(String hash) {
        String originalUrl = urlCacheRepository.getUrl(hash).orElse(
                urlRepository.findLongUrlByHash(hash).
                        orElseThrow(() -> new EntityNotFoundException("No url found for hash: " + hash))
        );
        return originalUrl;
    }
}
