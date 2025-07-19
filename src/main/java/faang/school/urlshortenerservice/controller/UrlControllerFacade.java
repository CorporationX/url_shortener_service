package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RedirectResponse;
import faang.school.urlshortenerservice.dto.ShortUrlRequest;
import faang.school.urlshortenerservice.dto.ShortUrlResponse;
import faang.school.urlshortenerservice.entity.ShortUrl;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlControllerFacade {

    @Value("${shorter.address}")
    private String shorterAddress;
    private final UrlService urlService;

    public RedirectResponse getResource(String hash) {
        ShortUrl shortUrl = urlService.getResource(hash);
        return new RedirectResponse(shortUrl.getActualUrl());
    }

    public ShortUrlResponse createShortUrl(ShortUrlRequest shortUrlRequest) {
        ShortUrl shortUrl = urlService.getShortUrl(shortUrlRequest);
        return createShortUrlResponse(shortUrl);
    }

    private ShortUrlResponse createShortUrlResponse(ShortUrl shortUrl) {
        String shortUrlValue = shorterAddress.concat(shortUrl.getHash());
        return new ShortUrlResponse(
                shortUrlValue,
                shortUrl.getExpirationTime());
    }
}
