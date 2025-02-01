package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
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
@RequiredArgsConstructor
@RequestMapping("/v1/url")
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/shortener")
    public ResponseEntity<UrlResponseDto> createShortUrl(@Validated @RequestBody UrlRequestDto urlRequestDto) {
        UrlResponseDto responseDto = urlService.processUrl(urlRequestDto.getUrl());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToUrl(@PathVariable String hash) {
        String longUrl = urlService.getUrlByHash(hash);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
    }
}
