package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cashe.HashCache;
import faang.school.urlshortenerservice.model.entity.UrlEntity;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UrlCacheRepository urlCacheRepository;

    @Autowired
    private HashCache hashCache;

    public String createShortUrl(String longUrl) {
        String hash = hashCache.getHash(longUrl);
        urlRepository.save(new UrlEntity(hash, longUrl));
        urlCacheRepository.save(hash, longUrl);
        return "http://short.url/" + hash;
    }
}