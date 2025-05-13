package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/")
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }

    @PostMapping
    public ResponseEntity<UrlResponseDto> createShortUrl(@Valid @RequestBody UrlRequestDto requestDto) {
        String shortUrl = urlService.createShortUrl(requestDto.getUrl());
        return ResponseEntity.ok(new UrlResponseDto(shortUrl));
    }
}
