package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.managers.HashCache;
import faang.school.urlshortenerservice.redis.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Data
@Slf4j
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
        log.info("Short URL hash created: {}, longUrl: {}", hash, longUrl);
        return "http://short.url/" + hash;
    }

    public String getOriginalUrl(String hash) {
        String originalUrl = urlCacheRepository.findUrlByHash(hash);
        if (originalUrl == null) {
            originalUrl = urlRepository.findUrlByHash(hash);
            urlCacheRepository.save(hash, originalUrl);
            if (originalUrl == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found");
            }
        }
        return originalUrl;
    }
}

