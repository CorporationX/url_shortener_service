package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/v1/url")
@RequiredArgsConstructor
@Slf4j
public class UrlController {
    private final UrlService urlService;
    private final UrlMapper urlMapper;

    @Value("${app.short-url-prefix}")
    private String shortUrlPrefix;

    @PostMapping
    public ResponseEntity<UrlReadDto> createShortUrl(@RequestBody @Valid UrlCreateDto urlCreateDto) {
        UrlReadDto urlReadDto = urlService.createShortUrl(urlCreateDto.getOriginalUrl());

        urlReadDto.setUrl(shortUrlPrefix + urlReadDto.getHash());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(urlReadDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        log.info("Перенаправление хэша {} на URL {}", hash, originalUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
