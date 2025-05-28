package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortenUrlRequest;
import faang.school.urlshortenerservice.dto.ShortenedUrlResponse;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequiredArgsConstructor
@RequestMapping("/url")
@Validated
@Tag(name = "URL Shortener", description = "API for creating short URLs")
public class UrlController {
    private final UrlService urlService;

    @Operation(
            summary = "Create short URL",
            description = "Converts a long URL into a compact short " +
                    "identifier that redirects to the original URL")
    @PostMapping
    public ResponseEntity<ShortenedUrlResponse> shortenUrl(
            @NotNull @Valid @RequestBody ShortenUrlRequest request
    ) {
        ShortenedUrlResponse shortUrl = urlService.shortenUrl(request);
        return ResponseEntity.ok(shortUrl);
    }

    @Operation(
            summary = "Redirect to original URL",
            description = "Performs redirect to the original URL associated with the short hash")
    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectByHash(
            @NotNull @PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}