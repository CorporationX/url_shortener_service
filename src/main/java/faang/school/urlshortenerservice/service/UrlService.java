package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.ShortUrl;
import faang.school.urlshortenerservice.exception.InvalidURLException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
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
    private final HashCache hashCache;

    @Setter
    @Value("${url-shortener.host-name}")
    private String hostName;

    public ShortUrlDto createShortUrl(LongUrlDto longUrl) {
        String url = longUrl.url();
        validateLongUrl(url);

        String hash = hashCache.getShortUrlFromCache();
        ShortUrl shortLongUrlPair = new ShortUrl(url, hash);

        urlRepository.save(shortLongUrlPair);
        urlCacheService.saveToCache(hash, url);

        String shortUrl = hostName.concat(hash);

        log.info("Short URL '{}' created for real URL '{}'", shortUrl, url);
        return new ShortUrlDto(shortUrl);
    }

    public LongUrlDto getUrl(String shortUrl) {
        validateShortUrl(shortUrl);
        String hash = parseHashFromShortUrl(shortUrl);

        Optional<String> urlFromCache = urlCacheService.getFromCache(hash);
        if (urlFromCache.isPresent()) {
            log.debug("Long URL {} returned from cache", urlFromCache.get());
            return new LongUrlDto(urlFromCache.get());
        }

        String urlFromDB = getUrlFromDataBase(hash);

        urlCacheService.saveToCache(hash, urlFromDB);

        log.debug("Long URL {} returned from DataBase", urlFromDB);
        return new LongUrlDto(urlFromDB);
    }

    private void validateLongUrl(String longUrl) {
        try {
            new URL(longUrl).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new InvalidURLException(String.format("Invalid URL provided: %s. Error: %s", longUrl, e.getMessage()));
        }
    }

    private void validateShortUrl(String shortUrl) {
        try {

            URL url = new URL(shortUrl);
            String shortUrlHost = url.getHost();
            String shortUrlPath = url.getPath();
            if (!hostName.contains(shortUrlHost)
                    || shortUrlPath.length() > 7
                    || shortUrlPath.isBlank()
                    || shortUrlPath.equals("/")) {
                throw new InvalidURLException(String.format("Invalid URL provided: %s", shortUrl));
            }
        } catch (MalformedURLException e) {
            throw new InvalidURLException(String.format("Invalid URL provided: %s. Error: %s", shortUrl, e.getMessage()));
        }
    }

    private String parseHashFromShortUrl(String shortUrl) {
        try {
            String urlPath = new URL(shortUrl).getPath();
            return urlPath.replaceFirst("/", "");
        } catch (MalformedURLException e) {
            throw new InvalidURLException(String.format("Invalid URL provided: %s. Error: %s", shortUrl, e.getMessage()));
        }
    }

    private String getUrlFromDataBase(String hash) {
        ShortUrl url = urlRepository.findById(hash).orElseThrow(() ->
                new EntityNotFoundException(String.format("URL matching hash %s not found", hash)));
        return url.getUrl();
    }
}
