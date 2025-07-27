package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.UrlProperties;
import faang.school.urlshortenerservice.exception.HashUnavailableException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepository cacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlProperties baseUrl;

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        return cacheRepository.find(hash)
                .orElseGet(() -> {
                    Url entity = urlRepository.findById(hash)
                            .orElseThrow(() -> new UrlNotFoundException(hash));
                    String longUrl = entity.getUrl();
                    cacheRepository.save(hash, longUrl);
                    return longUrl;
                });
    }

    @Transactional
    public String createShortUrl(String longUrl) {
        String hash = hashCache.getHash()
                .orElseThrow(() -> new HashUnavailableException());

        Url entity = new Url();
        entity.setHash(hash);
        entity.setUrl(longUrl);
        urlRepository.save(entity);

        cacheRepository.save(hash, longUrl);

        return baseUrl + "/" + hash;
    }
}
