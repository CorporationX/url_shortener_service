package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.model.dto.UrlRequest;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<String> createShortUrl(@RequestBody UrlRequest urlRequest) {
        String shortUrl = urlService.createShortUrl(urlRequest.getLongUrl());
        return ResponseEntity.ok(shortUrl);
    }
}
