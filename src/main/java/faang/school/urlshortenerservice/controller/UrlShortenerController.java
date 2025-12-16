package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.CreateUrlRequestDto;
import faang.school.urlshortenerservice.dto.CreateUrlResponseDto;
import faang.school.urlshortenerservice.service.UrlShortenerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@AllArgsConstructor
@Slf4j
@RequestMapping()
public class UrlShortenerController {
    private final UrlShortenerService urlShortenerService;

    @PostMapping("/url")
    public ResponseEntity<CreateUrlResponseDto> createShortUrl(
            @Valid
            @RequestBody
            CreateUrlRequestDto createUrlRequestDto) {
        log.info("Input long url {}", createUrlRequestDto.url());
        String shortUrl = urlShortenerService.createShortUrl(createUrlRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CreateUrlResponseDto.builder()
                        .shortUrl(shortUrl)
                        .build());
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getOriginalUrl(
            @PathVariable
            String hash) {
        String originalUrl = urlShortenerService.getOriginalUrl(hash);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
