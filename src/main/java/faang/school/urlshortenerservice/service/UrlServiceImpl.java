package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Override
    @Transactional
    public String shortenUrl(String longUrl) {
        log.info("Creating short URL for: {}", longUrl);

        String hash = hashCache.getHash();

        Url url = new Url();
        url.setHash(hash);
        url.setOriginalUrl(longUrl);

        urlRepository.save(url);
        urlCacheRepository.save(hash, longUrl);

        return hash;
    }

    @Override
    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        return urlCacheRepository.findOriginalUrlByHash(hash)
                .or(() -> urlRepository.findByHash(hash)
                        .map(urlEntity -> {
                            String originalUrl = urlEntity.getOriginalUrl();
                            urlCacheRepository.save(hash, originalUrl);
                            return originalUrl;
                        }))
                .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash));
    }
}
