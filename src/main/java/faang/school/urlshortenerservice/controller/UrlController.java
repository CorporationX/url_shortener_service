package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.url.CreateUrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<String> createShortUrl(@Valid @RequestBody CreateUrlDto createUrlDto) {
        return ResponseEntity.ok(urlService.createShortUrl(createUrlDto));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String hash) {
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(urlService.getOriginalUrl(hash))).build();
    }
}