package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.Service.UrlService;
import faang.school.urlshortenerservice.dto.UrlRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public String createShortUrl(@Valid @RequestBody UrlRequest urlRequest) {
        String shortUrl = urlService.createShortUrl(urlRequest.originalUrl());
        return shortUrl;
    }
}
