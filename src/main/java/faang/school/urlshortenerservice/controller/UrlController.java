package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<String> shortenUrl(@RequestBody UrlDto urlDto) {
        if (isValidUrl(urlDto.url())) {
            return ResponseEntity.badRequest().body("Invalid URL format.");
        }
        String shortUrl = urlService.createShortUrl(urlDto.url());
        return ResponseEntity.ok(shortUrl);
    }

    private boolean isValidUrl(String url) {
        try {
            new java.net.URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}