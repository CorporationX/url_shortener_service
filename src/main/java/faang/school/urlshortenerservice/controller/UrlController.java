package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlCreateRequest;
import faang.school.urlshortenerservice.dto.UrlCreateResponse;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/")
    public UrlCreateResponse createShortUrl(
            @RequestBody @Valid UrlCreateRequest request
    ) {
        String hash = urlService.createShortUrl(request.getUrl());
        return new UrlCreateResponse(hash);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {

        String originalUrl = urlService.getOriginalUrl(hash);

        return ResponseEntity
                .status(302)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}
