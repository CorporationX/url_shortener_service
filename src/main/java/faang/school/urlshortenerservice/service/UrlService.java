package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String createShortUrl(String longUrl) {
        String hash = hashCache.getHash();
        UrlEntity entity = new UrlEntity(hash,longUrl, LocalDateTime.now());

        urlRepository.save(entity);
        urlCacheRepository.save(hash,longUrl);

        return hash;
    }
}
