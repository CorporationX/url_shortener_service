package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.local_cache.LocalCache;
import faang.school.urlshortenerservice.validator.UrlServiceValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlServiceValidator urlServiceValidator;
    private final LocalCache localCache;
    private final UrlCacheRepository urlCacheRepository;
    private final String link = "http://localhost:8080/api/v1/shorter/redirect/";

    @Transactional
    public URL createNewShortUrl(URL url){
        String hash = localCache.getCache();

        Url entityUrl = new Url();
        entityUrl.setHash(hash);
        entityUrl.setUrl(url);

        urlRepository.save(entityUrl);
        urlCacheRepository.saveAtRedis(entityUrl);

        return createShortURLWithHash(hash);
    }

    public URL getUrl(String hash){
        Url url = urlRepository.findByHash(hash);
        return url.getUrl();
    }

    private URL convertStrToURL(String url){
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url");
        }
    }

    private URL createShortURLWithHash(String hash){
        String fullUrl = link.concat(hash);
        try {
            return new URL(fullUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Something went wrong when creating shortUrl");
        }
    }
}
