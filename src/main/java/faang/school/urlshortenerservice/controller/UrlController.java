package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<UrlResponse> create(@Valid @RequestBody UrlRequest request) {
        String shortUrl = urlService.shorten(request.getUrl());
        return ResponseEntity.ok(new UrlResponse(shortUrl));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String original = urlService.resolve(hash);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(original));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}