package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.Service.UrlService;
import faang.school.urlshortenerservice.dto.UrlRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public String createShortUrl(@Valid @RequestBody UrlRequest urlRequest) {
        return urlService.createShortUrl(urlRequest.originalUrl());
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginal(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
