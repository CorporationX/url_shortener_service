package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
@Validated
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/short")
    public ResponseEntity<String> shortenUrl(@Valid @RequestBody UrlRequest urlRequest) {
        String shortUrl = urlService.shortUrl(urlRequest.getUrl());
        return ResponseEntity.ok(shortUrl);
    }
}