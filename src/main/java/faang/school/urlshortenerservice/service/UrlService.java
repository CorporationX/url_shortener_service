package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.BadUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Service
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlRepository urlCacheRepository;

    public UrlService(HashCache hashCache,
                      @Qualifier("urlRepositoryImpl") UrlRepository urlRepository,
                      @Qualifier("redisUrlCacheRepositoryImpl") UrlRepository urlCacheRepository) {
        this.hashCache = hashCache;
        this.urlRepository = urlRepository;
        this.urlCacheRepository = urlCacheRepository;
    }

    public String getShortUrl(String longUrlString) {
        try {
            URL longUrl = new URL(longUrlString);
        } catch (MalformedURLException e) {
            throw new BadUrlException("Can not shorten url, because " + longUrlString + " is incorrect url");
        }
        String hash = hashCache.getHash();
        urlRepository.save(hash, longUrlString);
        urlCacheRepository.save(hash, longUrlString);
        URL shortUrl;
        try {
            shortUrl = new URL("http", "localhost", 8080, "/" + hash);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot shorten url");
        }
        return shortUrl.toExternalForm();
    }

    public String getUrlByHash(String hash) {
        Optional<String> url = urlCacheRepository.findUrlByHash(hash);
        if (url.isEmpty()) {
            url = urlRepository.findUrlByHash(hash);
            if (url.isEmpty()) {
                throw new UrlNotFoundException("Cannot find url by hash: " + hash);
            }
        }
        return url.get();
    }
}
