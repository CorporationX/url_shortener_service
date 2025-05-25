package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "URL Shortener API")
public class UrlController {
    private final UrlService urlService;

    @Operation(
            summary = "Create short URL",
            description = "Converts a long URL to a short URL and returns the short URL in Location header"
    )
    @PostMapping("/url")
    public ResponseEntity<Void> saveOriginalUrl(@RequestBody @Valid UrlCreateDto url) {
        String shortUrl = urlService.saveOriginalUrl(url.getUrl());
        return ResponseEntity.created(URI.create(shortUrl)).build();
    }

    @Operation(
            summary = "Redirect to original URL",
            description = "Takes a short URL hash and redirects to the original long URL"
    )
    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable("hash") @NotBlank String hash) {
        String result = urlService.getOriginalUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(result))
                .build();
    }
}
