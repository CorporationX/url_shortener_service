package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.ShortUrl;
import faang.school.urlshortenerservice.exception.InvalidURLException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Setter
    @Value("${url-shortener.host-name}")
    private String hostName;

    public ShortUrlDto createShortUrl(LongUrlDto longUrl) {
        String url = longUrl.url();
        validateUrl(url);

        String hash = hashCache.getShortUrlFromCache();
        ShortUrl shortLongUrlPair = new ShortUrl(url, hash);

        urlRepository.save(shortLongUrlPair);

        String shortUrl = hostName.concat(hash);

        log.info("Short URL '{}' created for real URL '{}'", shortUrl, url);
        return new ShortUrlDto(shortUrl);
    }

    private void validateUrl(String longUrl) {
        try {
            URL url = new URL(longUrl);
            url.toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new InvalidURLException(String.format("Invalid URL provided: %s. Error: %s", longUrl, e.getMessage()));
        }
    }
}
