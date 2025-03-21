package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<String> createShortUrl(@RequestBody @Valid UrlDto urlDto) {
        String originalUrl = urlDto.url();
        log.info("Received request to shorten URL: {}", originalUrl);

        String shortUrl = urlService.createShortUrl(originalUrl);
        log.info("Generated short URL: {}", shortUrl);

        return ResponseEntity.status(HttpStatus.CREATED).body(shortUrl);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<String> getOriginalUrl(@PathVariable String hash) {
        log.info("Received request to resolve hash: {}", hash);
        String originalUrl = urlService.getOriginalUrl(hash);
        log.debug("Resolved hash {} to URL: {}", hash, originalUrl);
        return ResponseEntity.ok(originalUrl);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<String> handleUrlNotFoundException(UrlNotFoundException ex) {
        log.warn("URL not found for hash: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}