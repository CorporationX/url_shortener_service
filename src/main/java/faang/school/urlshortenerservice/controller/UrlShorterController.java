package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlShorterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("short-url")
@RequiredArgsConstructor
public class UrlShorterController {
    private final UrlShorterService urlShorterService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShortUrlDto create(
            @RequestBody @Valid UrlDto urlDto
    ) {
        return urlShorterService.shortenUrl(urlDto.getUrl());
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortUrl) {
        String originalUrl = urlShorterService.getOriginalUrl(shortUrl);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody @Valid UrlDto urlDto) {
        urlShorterService.updateUrl(urlDto.getId(), urlDto.getUrl());
    }
}
