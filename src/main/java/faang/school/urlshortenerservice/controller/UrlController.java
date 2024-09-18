package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shortener")
public class UrlController {
    private static final Logger log = LoggerFactory.getLogger(UrlController.class);
    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<String> getShortUrl(@RequestBody @Valid UrlDto urlDto) {
        String shortUrl = urlService.getShortUrl(urlDto);
        return ResponseEntity
                .ok()
                .body(shortUrl);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getOriginalLink(@PathVariable String hash) {
        String originalLink = urlService.getOriginalLink(hash);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalLink))
                .build();
    }
}