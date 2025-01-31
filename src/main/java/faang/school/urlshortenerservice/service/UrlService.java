package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.entity.ShortUrl;
import faang.school.urlshortenerservice.exception.InvalidURLException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheService urlCacheService;
    private final HashCacheService hashCacheService;

    @Setter
    @Value("${url-shortener.host-name}")
    private String hostName;

    public String createShortUrl(LongUrlDto longUrl) {
        String url = longUrl.url();
        validateLongUrl(url);

        String hash = hashCacheService.getHashFromCache();
        ShortUrl shortLongUrlPair = ShortUrl.builder()
                .url(url)
                .hash(hash)
                .build();

        urlRepository.save(shortLongUrlPair);
        urlCacheService.saveToCache(hash, url);

        String shortUrl = hostName.concat(hash);

        log.info("Short URL '{}' created for real URL '{}'", shortUrl, url);
        return shortUrl;
    }

    public String getUrl(String hash) {

        Optional<String> urlFromCache = urlCacheService.getFromCache(hash);
        if (urlFromCache.isPresent()) {
            log.info("Long URL {} returned from cache", urlFromCache.get());
            return urlFromCache.get();
        }

        String urlFromDB = getUrlFromDataBase(hash);

        urlCacheService.saveToCache(hash, urlFromDB);

        log.info("Long URL {} returned from DataBase", urlFromDB);
        return urlFromDB;
    }

    private void validateLongUrl(String longUrl) {
        try {
            new URL(longUrl).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new InvalidURLException(String.format("Invalid URL provided: %s. Error: %s", longUrl, e.getMessage()));
        }
    }

    private String getUrlFromDataBase(String hash) {
        ShortUrl url = urlRepository.findById(hash).orElseThrow(() ->
                new EntityNotFoundException(String.format("URL matching provided hash '%s' not found", hash)));
        return url.getUrl();
    }
}
