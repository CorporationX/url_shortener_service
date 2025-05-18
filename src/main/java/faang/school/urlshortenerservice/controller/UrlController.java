package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${application.path}")
@Validated
public class UrlController {

    private final UrlService urlService;

    @Value("${application.domain}")
    private String domain;

    @PostMapping
    public ResponseEntity<Map<String, String>> createShortUrl(@Valid @RequestBody UrlDto urlDto) {
        String hash = urlService.getHash(urlDto.url());
        String shortUrl = String.format("%s/%s", domain, hash);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "shortUrl", shortUrl,
                        "hash", hash,
                        "originalUrl", urlDto.url()
                ));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable @NotNull String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
