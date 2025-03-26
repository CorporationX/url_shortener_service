package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        log.info("Received request to redirect hash: {}", hash);

        String originalUrl = urlService.getOriginalUrl(hash);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", originalUrl);

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PostMapping("/url")
    public ResponseEntity<String> createShortUrl(@Valid @RequestBody UrlRequest request) {
        String shortUrl = urlService.convertToShortUrl(request.getUrl());
        return ResponseEntity.ok(shortUrl);
    }
}