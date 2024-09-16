package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final String hostname;

    public UrlService(HashCache hashCache, UrlRepository urlRepository, UrlCacheRepository urlCacheRepository,
                      @Value("${url.hash.short-url.host}") String hostname) {
        this.hashCache = hashCache;
        this.urlRepository = urlRepository;
        this.urlCacheRepository = urlCacheRepository;
        this.hostname = hostname;
    }

    @Transactional
    public String shortenUrl(String longUrl) {
        String hash = hashCache.getHash();
        urlRepository.save(hash, longUrl);
        urlCacheRepository.saveUrl(hash, longUrl);
        return String.format("%s/%s", hostname, hash);
    }

    public String getLongUrl(String hash) {
        String url = urlCacheRepository.getUrl(hash);
        if (url != null) {
            return url;
        }

        url = urlRepository.findUrl(hash);
        if (url != null) {
            urlCacheRepository.saveUrl(hash, url);
            return url;
        }

        throw new UrlNotFoundException(String.format("URL not found for hash: %s", hash));
    }
}
