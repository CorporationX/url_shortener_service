package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.ShortUrlProperties;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
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
public class UrlController {
    private final UrlService service;
    private final ShortUrlProperties urlProperties;

    @PostMapping("/url")
    public UrlResponseDto shortenLink(@Valid @RequestBody UrlRequestDto urlDto) {
        String hash = service.createShortUrl(urlDto);

        return new UrlResponseDto(
                String.format("%s/%s", urlProperties.getBaseUrl(), hash)
        );
    }

    @GetMapping("/{hash:[a-zA-Z0-9]{1,6}}")
    public ResponseEntity<Void> redirectToLongLink(@PathVariable String hash) {
        String originalUrl = service.getOriginalUrl(hash);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
