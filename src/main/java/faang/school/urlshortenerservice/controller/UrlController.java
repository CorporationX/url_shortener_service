package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.model.dto.UrlRequest;
import faang.school.urlshortenerservice.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/url")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @PostMapping
    public ResponseEntity<String> createShortUrl(@Validated @RequestBody UrlRequest urlRequest) {
        String shortUrl = urlService.createShortUrl(urlRequest.getLongUrl());
        return ResponseEntity.ok(shortUrl);
    }
}
