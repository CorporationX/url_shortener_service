package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${base.url}/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private final UrlValidator urlValidator;

    @PostMapping
    public ResponseEntity<String> createShortUrl(@Valid @RequestBody UrlRequestDto urlRequest) {
        if (!urlValidator.isValid(urlRequest.getOriginalUrl())) {
            throw new IllegalArgumentException("Invalid URL");
        }
        String shortUrl = urlService.createShortUrl(urlRequest);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<String> getLongUrl(@PathVariable String hash) {
        String longUrl = urlService.getLongUrl(hash);
        return ResponseEntity.ok(longUrl);
    }
}