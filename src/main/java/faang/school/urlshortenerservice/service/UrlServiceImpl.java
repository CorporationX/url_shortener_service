package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String createShortUrl(String originalUrl) {
        String hash = hashCache.getHash();

        Url entity = Url.builder()
                .hash(hash)
                .originalUrl(originalUrl)
                .build();

        urlRepository.save(entity);

        urlCacheRepository.save(hash, originalUrl);

        return hash;
    }

    public String getOriginalUrl(String hash) {

        return urlCacheRepository.findByHash(hash)
                .orElseGet(() ->
                        urlRepository.findById(hash)
                                .map(entity -> {
                                    urlCacheRepository.save(hash, entity.getOriginalUrl());
                                    return entity.getOriginalUrl();
                                })
                                .orElseThrow(() -> new UrlNotFoundException(hash))
                );
    }
}
