package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.managers.HashCache;
import faang.school.urlshortenerservice.redis.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Data
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Transactional
    public String createShortUrl(String longUrl) {
        String hash = hashCache.getHash();
        Url url = new Url(hash, longUrl);
        urlRepository.save(url);
        urlCacheRepository.save(hash, longUrl);
        return "http://short.url/" + hash;
    }
}

