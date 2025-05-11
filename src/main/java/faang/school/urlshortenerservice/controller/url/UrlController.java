package faang.school.urlshortenerservice.controller.url;

import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.dto.url.UrlResponseDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<UrlResponseDto> generateShortUrl(
            @RequestBody @Valid UrlRequestDto urlRequestDto
    ) {
        log.info("Received request to generate short URL for: {}", urlRequestDto.getUrl());
        UrlResponseDto response = urlService.createShortUrl(urlRequestDto);
        log.info("Successfully generated short URL: {} for original URL: {}",
                response.getHash(), urlRequestDto.getUrl());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<UrlResponseDto> getOriginalUrl(
            @PathVariable String hash
    ) {
        log.info("Received request to resolve short URL with hash: {}", hash);
        UrlResponseDto response = urlService.getOriginalUrl(hash);
        log.info("Successfully resolved hash: {} to original URL: {}",
                hash, response.getUrl());
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(response);
    }
}
