package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.WrongUrl;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.model.UrlEntity;
import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashCache hashCache;
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final UrlShortenerProperties urlShortenerProperties;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String shorten(String longUrl) {
        validateUrl(longUrl);

        String hash = hashCache.getHash();

        UrlEntity entity = new UrlEntity();
        entity.setUrl(longUrl);
        entity.setHash(hash);
        urlRepository.save(entity);

        urlCacheRepository.put(hash, longUrl);
        String shortUrl = createShortUrl(hash);
        log.info("Create shortened url {} <- {}", shortUrl, longUrl);

        return shortUrl;
    }

    @Transactional
    public String getUrl(String hash) {
        String longUrl = urlCacheRepository.get(hash);
        if (Objects.nonNull(longUrl)) {
            return longUrl;
        }
        UrlEntity currentUrl =  urlRepository.findById(hash).orElseThrow();
        String currentLongUrl = currentUrl.getUrl();

        log.info("Get original url {} <- {}", hash, currentLongUrl);

        return currentLongUrl;
    }

    private void validateUrl(String longUrl) {
        try{
            new URL(longUrl);
        }catch (MalformedURLException e) {
            log.error("Invalid url: {}", longUrl);
            throw new WrongUrl("Invalid url: " + longUrl);
        }
    }

    private String createShortUrl(String hash) {
        String protocol = urlShortenerProperties.getProtocol();
        String domain = urlShortenerProperties.getDomain();
        return protocol + "://" + domain + "/" + hash;
    }
}
