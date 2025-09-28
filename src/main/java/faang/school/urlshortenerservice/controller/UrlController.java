package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlHashDto;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/url")
@Validated
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public String createHash(@Valid @RequestBody UrlHashDto urlHashDto) {
        return urlService.create(urlHashDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Object> getUrl(@PathVariable("hash") @NotEmpty(message = "the hash must not be " +
            "empty") String hash) {
        return ResponseEntity.
                status(302).
                header("Location", urlService.find(hash))
                .build();
    }

    @PostMapping
    public ResponseEntity<UrlResponseDto> createShortUrl(@Valid @RequestBody UrlRequestDto urlRequestDto) {
        log.info("Received request: create short URL for: {}", urlRequestDto.getUrl());

        UrlResponseDto responseUrl = urlService.createShortUrl(urlRequestDto.getUrl());

        log.info("Short URL was successfully created : {} for basic URL: {}",
                responseUrl.getShortUrl(), responseUrl.getTruelUrl());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUrl);
    }

}
