package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.local.cache.LocalCache;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    @Value("${hash.cache.ttl-in-hours:24}")
    private int ttlInHours;

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final LocalCache localCache;

    @Override
    @Transactional
    public String getUrlByHash(String hash) {
        String url = urlCacheRepository.findUrlByHash(hash);
        if (url != null) {
            urlCacheRepository.saveUrlWithExpiry(hash, url, ttlInHours);
            return url;
        }

        url = urlRepository.findById(hash)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash))
                .getUrl();
        urlCacheRepository.saveUrlWithExpiry(hash, url, ttlInHours);
        return url;
    }

    @Override
    @Transactional
    public String createShortUrl(String originalUrl) {
        String hash = localCache.getHash();
        Url url = new Url(hash, originalUrl);
        urlRepository.save(url);
        urlCacheRepository.saveUrlWithExpiry(hash, originalUrl, ttlInHours);
        return hash;
    }

    @Override
    @Transactional
    public void cleanUrl() {
        List<String> freedHashes = urlRepository.deleteOldUrlsAndReturnHashes();

        if (!freedHashes.isEmpty()) {
            List<Hash> hashEntities = freedHashes.stream()
                    .map(Hash::new)
                    .collect(Collectors.toList());

            hashRepository.saveAll(hashEntities);
        }
    }
}
