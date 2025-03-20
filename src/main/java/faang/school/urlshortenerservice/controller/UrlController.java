package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<String> createShortUrl(@Valid @RequestBody UrlDto urlDto) {
        String shortUrl = urlService.createShortUrl(urlDto.getUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(shortUrl);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build();
    }
}
