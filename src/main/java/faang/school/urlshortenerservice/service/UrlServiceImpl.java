package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCacheImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCacheImpl hashCache;

    @Override
    public String getLongUrl(String hash) {
        return urlCacheRepository.get(hash)
                .or(() -> urlRepository.findByHash(hash))
                .map(url -> {
                    urlCacheRepository.save(hash, url);
                    return url;
                })
                .orElseThrow(() -> new EntityNotFoundException("URL not found for hash: " + hash));
    }

    @Override
    public String getShortUrl(String url) {
        String hash = hashCache.getHash();

        urlRepository.save(Url.builder()
                .hash(hash)
                .url(url)
                .build());

        urlCacheRepository.save(hash, url);

        return hash;
    }
}
