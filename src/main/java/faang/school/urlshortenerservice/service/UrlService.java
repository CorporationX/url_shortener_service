package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueIdRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final LocalCache localCache;
    private final HashRepository hashRepository;
    private final UniqueIdRepository uniqueIdRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String createShortUrl(String longUrl) {
        String shortUrl = localCache.getHash();
        Hash hash = new Hash(shortUrl);
        hashRepository.save(hash);
        Url url = new Url(shortUrl, longUrl);
        uniqueIdRepository.save(url);
        urlCacheRepository.save(shortUrl, longUrl);
        return shortUrl;
    }
}