package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.redis.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public String createShortUrl(String longUrl) {
        String hash = hashCache.getHash();
        Url url = new Url(hash, longUrl, null);

        urlRepository.save(url);
        urlCacheRepository.save(hash, longUrl);

        return baseUrl + hash;
    }

    public String getOriginalUrl(String hash) {
        return urlCacheRepository.findByHash(hash)
                .orElseGet(() -> {
                    Url urlEntity = urlRepository.findById(hash)
                            .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash));
                    urlCacheRepository.save(hash, urlEntity.getUrl());
                    return urlEntity.getUrl();
                });
    }
}
