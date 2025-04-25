package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
public class UrlShortenerController {
    private final UrlShortenerService urlShortenerService;

    @PostMapping
    public ResponseEntity<String> createShortenedUrl(@RequestParam String originalUrl) {
        return ResponseEntity.ok(urlShortenerService.createShortUrl(originalUrl));
    }

    @GetMapping()
    public ResponseEntity<Void> getOriginalUrl(@RequestParam String shortUrl) {
        shortUrl = "http://CorporationX/" + shortUrl;
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlShortenerService.getOriginalUrl(shortUrl)))
                .build();
    }
}
