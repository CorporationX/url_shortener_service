package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Objects;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url")
public class UrlController {

    private final UrlService urlService;

    @PostMapping()
    public String createShortUrl(@RequestBody @Valid UrlDto url) {
        log.info("Получили запрос {}, на создание короткой ссылки.", url.getUrl());
        return urlService.getShortenUrl(url);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToUrl(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        log.info("Получен запрос на перенаправление на исходный URL: {}", originalUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(Objects.requireNonNullElse(originalUrl, "/")));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}