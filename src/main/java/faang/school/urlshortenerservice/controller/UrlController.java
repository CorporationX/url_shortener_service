package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.url.UrlService;
import faang.school.urlshortenerservice.validation.url.Url;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/shortener/")
@RequiredArgsConstructor
@Validated
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String hash) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(urlService.getLongUrl(hash)))
                .build();
    }

    @PostMapping("/url")
    public ResponseEntity<Void> redirectToShortUrl(@RequestBody @NotNull @Url String url) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlService.getShortUrl(url)))
                .build();
    }
}
