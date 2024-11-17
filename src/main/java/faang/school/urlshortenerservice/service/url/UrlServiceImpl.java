package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.RedisCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.cache.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final RedisCache redisCache;
    private final HashCache hashCache;

    @Override
    public String getLongUrl(String hash) {
        return redisCache.getFromCache(hash)
                .or(() -> urlRepository.findUrlByHash(hash))
                .orElseThrow(() -> new UrlNotFoundException("URL not found by hash %s".formatted(hash)));
    }

    @Override
    public String getShortUrl(String url) {
        String hashFromCache = hashCache.getHash();

        redisCache.saveToCache(hashFromCache, url);
        urlRepository.save(Url.builder()
                .hash(hashFromCache)
                .url(url)
                .build());

        return url;
    }
}
