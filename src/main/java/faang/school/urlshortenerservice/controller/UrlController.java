package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<String> createShortUrl(@Valid @RequestBody UrlRequest urlRequest) {
        String originalUrl = urlRequest.getUrl();
        String hash = urlService.createShortUrl(originalUrl);
        String shortUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath() + hash;
        return ResponseEntity.created(URI.create(shortUrl)).body(shortUrl);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}