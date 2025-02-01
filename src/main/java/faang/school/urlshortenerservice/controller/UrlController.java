package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<String> shortenUrl(@RequestBody UrlDto urlDto) {
        if (!isValidUrl(urlDto.url())) {
            return ResponseEntity.badRequest().body("Invalid URL format.");
        }
        String shortUrl = urlService.createShortUrlAndSave(urlDto.url());
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToUrl(@PathVariable String hash) {
        String longUrl = urlService.getUrlByHash(hash);
        return ResponseEntity.status(302)
                .header("Location", longUrl)
                .build();
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