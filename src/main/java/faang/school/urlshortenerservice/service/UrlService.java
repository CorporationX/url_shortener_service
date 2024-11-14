package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.IncorrectUrl;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.model.UrlEntity;
import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import faang.school.urlshortenerservice.repository.URLCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlShortenerProperties urlShortenerProperties;
    private final URLCacheRepository urlCacheRepository;

    @Transactional
    public String shorten(String longUrl) {
        validateUrl(longUrl);

        String hash = hashCache.getHash();

        UrlEntity entity = new UrlEntity();
        entity.setUrlValue(longUrl);
        entity.setHashValue(hash);
        urlRepository.save(entity);

        urlCacheRepository.put(hash, longUrl);

        return createShortUrl(hash);
    }

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
       String longUrl = urlCacheRepository.get(hash);
       if (Objects.nonNull(longUrl)) {
           return longUrl;
       }
       UrlEntity currentUrl = urlRepository.findById(hash).orElseThrow();
       return currentUrl.getUrlValue();
    }

    private void validateUrl(String longUrl) {
        try {
            new URL(longUrl);
        } catch (MalformedURLException e) {
            throw new IncorrectUrl("Incorrect URL: " + longUrl);
        }
    }

    private String createShortUrl(String hash) {
        String protocol = urlShortenerProperties.getProtocol();
        String domain = urlShortenerProperties.getDomain();
        return protocol + "://" + domain + "/" + hash;
    }
}
