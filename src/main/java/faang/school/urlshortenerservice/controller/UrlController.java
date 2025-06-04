package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/url")
public class UrlController {

    @Value("${url.base-url}")
    private String baseUrl;

    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<UrlResponseDto> createShortUrl(@RequestBody @Valid UrlRequestDto urlRequestDto) {
        String hash = urlService.shortenUrl(urlRequestDto.getUrl());
        return ResponseEntity.ok(new UrlResponseDto(baseUrl + "/" + hash));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
