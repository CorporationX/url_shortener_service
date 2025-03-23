package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody UrlRequest urlRequest) {
        String shortUrl = urlService.createShortUrl(urlRequest.getUrl());
        return new ResponseEntity<>(new UrlResponse(shortUrl), HttpStatus.CREATED);
    }
}
