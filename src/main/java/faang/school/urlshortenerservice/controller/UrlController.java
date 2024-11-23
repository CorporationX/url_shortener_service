package faang.school.urlshortenerservice.controller;


import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url-shortener/")
@Validated
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String hash) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlService.getLongUrlByHash(hash)))
                .build();
    }

    @PostMapping("/url")
    public ResponseEntity<Void> redirectToShortUrl(@RequestBody UrlDto urlDto) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlService.getShortUrlByHash(urlDto.getUrl())))
                .build();
    }
}
