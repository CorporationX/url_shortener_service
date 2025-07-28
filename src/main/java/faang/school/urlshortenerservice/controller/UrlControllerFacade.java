package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RedirectResponse;
import faang.school.urlshortenerservice.dto.ShortUrlRequest;
import faang.school.urlshortenerservice.dto.ShortUrlResponse;
import faang.school.urlshortenerservice.entity.ShortUrl;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class UrlControllerFacade {

    @Value("${shorter.address}")
    private String shorterAddress;
    private final UrlService urlService;

    public RedirectResponse getActualUrl(String hash) {
        String actualUrl = urlService.getActualUrl(hash).getActualUrl();
        return new RedirectResponse(URI.create(actualUrl));
    }

    public ShortUrlResponse createShortUrl(ShortUrlRequest shortUrlRequest) {
        ShortUrl shortUrl = urlService.getShortUrl(shortUrlRequest);
        return createShortUrlResponse(shortUrl);
    }

    private ShortUrlResponse createShortUrlResponse(ShortUrl shortUrl) {
        URI shortUrlValue = URI.create(shorterAddress.concat(shortUrl.getHash()));
        return new ShortUrlResponse(
                shortUrlValue,
                shortUrl.getExpirationTime());
    }
}